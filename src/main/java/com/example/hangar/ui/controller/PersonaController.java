package com.example.hangar.ui.controller;

import com.example.hangar.model.Persona;
import com.example.hangar.service.PersonaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component
public class PersonaController {

    private final PersonaService personaService;
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();
    private final FilteredList<Persona> filteredPersonas = new FilteredList<>(personas, persona -> true);

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @FXML
    private TableView<Persona> personaTable;

    @FXML
    private TableColumn<Persona, Integer> idColumn;

    @FXML
    private TableColumn<Persona, String> nombreColumn;

    @FXML
    private TableColumn<Persona, String> curpColumn;

    @FXML
    private TableColumn<Persona, Integer> edadColumn;

    @FXML
    private TableColumn<Persona, String> celularColumn;

    @FXML
    private TableColumn<Persona, Integer> hrsVueloColumn;

    @FXML
    private TableColumn<Persona, String> licenciaColumn;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField curpField;

    @FXML
    private TextField edadField;

    @FXML
    private TextField celularField;

    @FXML
    private TextField hrsVueloField;

    @FXML
    private TextField licenciaField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (personaTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idPersona"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            curpColumn.setCellValueFactory(new PropertyValueFactory<>("curp"));
            edadColumn.setCellValueFactory(new PropertyValueFactory<>("edad"));
            celularColumn.setCellValueFactory(new PropertyValueFactory<>("celular"));
            hrsVueloColumn.setCellValueFactory(new PropertyValueFactory<>("hrsVuelo"));
            licenciaColumn.setCellValueFactory(new PropertyValueFactory<>("licencia"));
            personas.setAll(personaService.findAll());
            personaTable.setItems(filteredPersonas);
            personaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "El nombre es obligatorio.");
            return;
        }
        Persona selected = personaTable.getSelectionModel().getSelectedItem();
        Persona persona = selected != null ? personaService.findById(selected.getIdPersona()) : new Persona();

        persona.setNombre(nombreField.getText().trim());
        persona.setCurp(curpField.getText().trim());
        persona.setCelular(celularField.getText().trim());
        persona.setLicencia(licenciaField.getText().trim());

        // Validar y establecer edad
        String edadText = edadField.getText().trim();
        if (!edadText.isEmpty()) {
            try {
                persona.setEdad(Integer.parseInt(edadText));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Formato incorrecto", "La edad debe ser un número entero.");
                return;
            }
        } else {
            persona.setEdad(null);
        }

        // Validar y establecer horas de vuelo
        String hrsVueloText = hrsVueloField.getText().trim();
        if (!hrsVueloText.isEmpty()) {
            try {
                persona.setHrsVuelo(Integer.parseInt(hrsVueloText));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Formato incorrecto", "Las horas de vuelo deben ser un número entero.");
                return;
            }
        } else {
            persona.setHrsVuelo(null);
        }

        personaService.save(persona);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "La persona ha sido guardada correctamente.");
    }

    @FXML
    private void onEliminar() {
        Persona selected = personaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir una persona para eliminarla.");
            return;
        }

        try {
            String constraintMessage = personaService.checkDeletionConstraints(selected.getIdPersona());
            if (constraintMessage != null) {
                showAlert(Alert.AlertType.WARNING, "No se puede eliminar", constraintMessage);
                return;
            }

            personaService.delete(selected.getIdPersona());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "La persona seleccionada fue eliminada.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar esta persona porque tiene roles, pilotos, tripulaciones u otros registros asociados.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar la persona: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (personaTable != null) {
            personaTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredPersonas.setPredicate(persona -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        Integer numericTerm = null;
        try {
            numericTerm = Integer.parseInt(term.trim());
        } catch (NumberFormatException ignored) {
        }
        Integer finalNumericTerm = numericTerm;
        filteredPersonas.setPredicate(persona -> {
            if (persona == null) {
                return false;
            }
            boolean matchesNombre = persona.getNombre() != null && persona.getNombre().toLowerCase().contains(normalized);
            boolean matchesCurp = persona.getCurp() != null && persona.getCurp().toLowerCase().contains(normalized);
            boolean matchesCelular = persona.getCelular() != null && persona.getCelular().toLowerCase().contains(normalized);
            boolean matchesLicencia = persona.getLicencia() != null && persona.getLicencia().toLowerCase().contains(normalized);
            boolean matchesEdad = finalNumericTerm != null && persona.getEdad() != null && persona.getEdad().equals(finalNumericTerm);
            boolean matchesHrsVuelo = finalNumericTerm != null && persona.getHrsVuelo() != null && persona.getHrsVuelo().equals(finalNumericTerm);
            return matchesNombre || matchesCurp || matchesCelular || matchesLicencia || matchesEdad || matchesHrsVuelo;
        });
    }

    private boolean isFormValid() {
        return nombreField != null && !nombreField.getText().isBlank();
    }

    private void refreshTable() {
        personas.setAll(personaService.findAll());
        personaTable.refresh();
        onBuscar();
    }

    private void fillForm(Persona persona) {
        if (persona == null) {
            clearForm();
            return;
        }
        nombreField.setText(persona.getNombre());
        curpField.setText(persona.getCurp());
        celularField.setText(persona.getCelular());
        licenciaField.setText(persona.getLicencia());
        edadField.setText(persona.getEdad() != null ? persona.getEdad().toString() : "");
        hrsVueloField.setText(persona.getHrsVuelo() != null ? persona.getHrsVuelo().toString() : "");
    }

    private void clearForm() {
        if (nombreField != null) nombreField.clear();
        if (curpField != null) curpField.clear();
        if (edadField != null) edadField.clear();
        if (celularField != null) celularField.clear();
        if (hrsVueloField != null) hrsVueloField.clear();
        if (licenciaField != null) licenciaField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

