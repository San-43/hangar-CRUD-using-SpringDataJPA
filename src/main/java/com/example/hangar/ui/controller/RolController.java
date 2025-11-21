package com.example.hangar.ui.controller;

import com.example.hangar.model.Persona;
import com.example.hangar.model.Rol;
import com.example.hangar.service.PersonaService;
import com.example.hangar.service.RolService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class RolController {

    private final RolService rolService;
    private final PersonaService personaService;
    private final ObservableList<Rol> roles = FXCollections.observableArrayList();
    private final FilteredList<Rol> filteredRoles = new FilteredList<>(roles, rol -> true);
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();

    public RolController(RolService rolService, PersonaService personaService) {
        this.rolService = rolService;
        this.personaService = personaService;
    }

    @FXML
    private TableView<Rol> rolTable;

    @FXML
    private TableColumn<Rol, Integer> idColumn;

    @FXML
    private TableColumn<Rol, String> personaColumn;

    @FXML
    private TableColumn<Rol, String> rolColumn;

    @FXML
    private ComboBox<Persona> personaCombo;

    @FXML
    private TextField rolField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (rolTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idRol"));
            personaColumn.setCellValueFactory(cellData -> {
                Persona persona = cellData.getValue().getPersona();
                return new SimpleStringProperty(persona != null ? persona.getNombre() : "");
            });
            rolColumn.setCellValueFactory(new PropertyValueFactory<>("rol"));
            roles.setAll(rolService.findAll());
            rolTable.setItems(filteredRoles);
            rolTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
        populatePersonas();
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Seleccione una persona y escriba el rol.");
            return;
        }
        Rol selected = rolTable.getSelectionModel().getSelectedItem();
        Rol rol = selected != null ? rolService.findById(selected.getIdRol()) : new Rol();

        rol.setPersona(personaCombo.getValue());
        rol.setRol(rolField.getText().trim());

        rolService.save(rol);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "El rol ha sido guardado correctamente.");
    }

    @FXML
    private void onEliminar() {
        Rol selected = rolTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un rol para eliminarlo.");
            return;
        }

        try {
            String constraintMessage = rolService.checkDeletionConstraints(selected.getIdRol());
            if (constraintMessage != null) {
                showAlert(Alert.AlertType.WARNING, "No se puede eliminar", constraintMessage);
                return;
            }

            rolService.delete(selected.getIdRol());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El rol seleccionado fue eliminado.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar este rol porque tiene tripulaciones u otros registros asociados.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar el rol: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (rolTable != null) {
            rolTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredRoles.setPredicate(rol -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredRoles.setPredicate(rol -> {
            if (rol == null) {
                return false;
            }
            boolean matchesRol = rol.getRol() != null && rol.getRol().toLowerCase().contains(normalized);
            boolean matchesPersona = rol.getPersona() != null && rol.getPersona().getNombre() != null
                    && rol.getPersona().getNombre().toLowerCase().contains(normalized);
            return matchesRol || matchesPersona;
        });
    }

    private void populatePersonas() {
        personas.setAll(personaService.findAll());
        personas.sort(Comparator.comparing(p -> p.getNombre() != null ? p.getNombre() : "", String.CASE_INSENSITIVE_ORDER));
        if (personaCombo == null) {
            return;
        }
        personaCombo.setItems(personas);
        personaCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Persona item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        personaCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Persona item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
    }

    private boolean isFormValid() {
        return personaCombo != null && personaCombo.getValue() != null
                && rolField != null && !rolField.getText().isBlank();
    }

    private void refreshTable() {
        roles.setAll(rolService.findAll());
        rolTable.refresh();
        onBuscar();
    }

    private void fillForm(Rol rol) {
        if (rol == null) {
            clearForm();
            return;
        }
        rolField.setText(rol.getRol());
        if (rol.getPersona() != null) {
            personaCombo.setValue(personas.stream()
                    .filter(p -> p.getIdPersona().equals(rol.getPersona().getIdPersona()))
                    .findFirst()
                    .orElse(null));
        }
    }

    private void clearForm() {
        if (rolField != null) rolField.clear();
        if (personaCombo != null) personaCombo.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

