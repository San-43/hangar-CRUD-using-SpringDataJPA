package com.example.hangar.ui.controller;

import com.example.hangar.model.Rol;
import com.example.hangar.service.RolService;
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
public class RolController {

    private final RolService rolService;
    private final ObservableList<Rol> roles = FXCollections.observableArrayList();
    private final FilteredList<Rol> filteredRoles = new FilteredList<>(roles, rol -> true);

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @FXML
    private TableView<Rol> rolTable;

    @FXML
    private TableColumn<Rol, Long> idColumn;

    @FXML
    private TableColumn<Rol, String> nombreColumn;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (rolTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            roles.setAll(rolService.findAll());
            rolTable.setItems(filteredRoles);
            rolTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "El nombre es obligatorio.");
            return;
        }
        Rol selected = rolTable.getSelectionModel().getSelectedItem();
        Rol rol = selected != null ? rolService.findById(selected.getId()) : new Rol();
        rol.setNombre(nombreField.getText().trim());
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
        rolService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El rol seleccionado fue eliminado.");
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
            boolean matchesNombre = rol.getNombre() != null && rol.getNombre().toLowerCase().contains(normalized);
            return matchesNombre;
        });
    }

    private boolean isFormValid() {
        return nombreField != null && !nombreField.getText().isBlank();
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
        nombreField.setText(rol.getNombre());
    }

    private void clearForm() {
        if (nombreField != null) {
            nombreField.clear();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

