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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component
public class PersonaController {

    private final PersonaService personaService;
    private final RolService rolService;
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();
    private final FilteredList<Persona> filteredPersonas = new FilteredList<>(personas, persona -> true);
    private final ObservableList<Rol> roles = FXCollections.observableArrayList();

    public PersonaController(PersonaService personaService, RolService rolService) {
        this.personaService = personaService;
        this.rolService = rolService;
    }

    @FXML
    private TableView<Persona> personaTable;

    @FXML
    private TableColumn<Persona, String> nombreColumn;

    @FXML
    private TableColumn<Persona, String> documentoColumn;

    @FXML
    private TableColumn<Persona, String> rolColumn;

    @FXML
    private TextField nombresField;

    @FXML
    private TextField apellidosField;

    @FXML
    private TextField documentoField;

    @FXML
    private ComboBox<Rol> rolCombo;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (personaTable != null) {
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombres"));
            documentoColumn.setCellValueFactory(new PropertyValueFactory<>("documento"));
            rolColumn.setCellValueFactory(data -> new SimpleStringProperty(
                    data.getValue().getRol() != null ? data.getValue().getRol().getNombre() : ""));
            personas.setAll(personaService.findAll());
            personaTable.setItems(filteredPersonas);
            personaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }

        if (rolCombo != null) {
            roles.setAll(rolService.findAll());
            rolCombo.setItems(roles);
            rolCombo.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(Rol rol, boolean empty) {
                    super.updateItem(rol, empty);
                    setText(empty || rol == null ? null : rol.getNombre());
                }
            });
            rolCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Rol rol, boolean empty) {
                    super.updateItem(rol, empty);
                    setText(empty || rol == null ? null : rol.getNombre());
                }
            });
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Todos los campos son obligatorios.");
            return;
        }

        Persona selected = personaTable.getSelectionModel().getSelectedItem();
        Persona persona = selected != null ? personaService.findById(selected.getId()) : new Persona();
        persona.setNombres(nombresField.getText().trim());
        persona.setApellidos(apellidosField.getText().trim());
        persona.setDocumento(documentoField.getText().trim());
        persona.setRol(rolCombo.getValue());
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
        personaService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "La persona seleccionada fue eliminada.");
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        personaTable.getSelectionModel().clearSelection();
    }

    private boolean isFormValid() {
        return nombresField != null && !nombresField.getText().isBlank()
                && apellidosField != null && !apellidosField.getText().isBlank()
                && documentoField != null && !documentoField.getText().isBlank()
                && rolCombo != null && rolCombo.getValue() != null;
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
        nombresField.setText(persona.getNombres());
        apellidosField.setText(persona.getApellidos());
        documentoField.setText(persona.getDocumento());
        rolCombo.getSelectionModel().select(persona.getRol());
    }

    private void clearForm() {
        if (nombresField != null) {
            nombresField.clear();
        }
        if (apellidosField != null) {
            apellidosField.clear();
        }
        if (documentoField != null) {
            documentoField.clear();
        }
        if (rolCombo != null) {
            rolCombo.getSelectionModel().clearSelection();
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
        filteredPersonas.setPredicate(persona -> {
            if (persona == null) {
                return false;
            }
            boolean matchesNombre = persona.getNombres() != null && persona.getNombres().toLowerCase().contains(normalized);
            boolean matchesApellido = persona.getApellidos() != null && persona.getApellidos().toLowerCase().contains(normalized);
            boolean matchesDocumento = persona.getDocumento() != null && persona.getDocumento().toLowerCase().contains(normalized);
            boolean matchesRol = persona.getRol() != null && persona.getRol().getNombre() != null
                    && persona.getRol().getNombre().toLowerCase().contains(normalized);
            return matchesNombre || matchesApellido || matchesDocumento || matchesRol;
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
