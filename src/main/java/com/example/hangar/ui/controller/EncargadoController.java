package com.example.hangar.ui.controller;

import com.example.hangar.model.Encargado;
import com.example.hangar.service.EncargadoService;
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
public class EncargadoController {

    private final EncargadoService encargadoService;
    private final ObservableList<Encargado> encargados = FXCollections.observableArrayList();
    private final FilteredList<Encargado> filteredEncargados = new FilteredList<>(encargados, encargado -> true);

    public EncargadoController(EncargadoService encargadoService) {
        this.encargadoService = encargadoService;
    }

    @FXML
    private TableView<Encargado> encargadoTable;

    @FXML
    private TableColumn<Encargado, Integer> idColumn;

    @FXML
    private TableColumn<Encargado, String> nombreColumn;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (encargadoTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idEncargado"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            encargados.setAll(encargadoService.findAll());
            encargadoTable.setItems(filteredEncargados);
            encargadoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "El nombre es obligatorio.");
            return;
        }
        Encargado selected = encargadoTable.getSelectionModel().getSelectedItem();
        Encargado encargado = selected != null ? encargadoService.findById(selected.getIdEncargado()) : new Encargado();
        encargado.setNombre(nombreField.getText().trim());
        encargadoService.save(encargado);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "El encargado ha sido guardado correctamente.");
    }

    @FXML
    private void onEliminar() {
        Encargado selected = encargadoTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un encargado para eliminarlo.");
            return;
        }

        try {
            encargadoService.delete(selected.getIdEncargado());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El encargado seleccionado fue eliminado.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar este encargado porque está asignado a talleres o reportes. " +
                    "Primero debe eliminar o reasignar los registros relacionados.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar el encargado: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (encargadoTable != null) {
            encargadoTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredEncargados.setPredicate(encargado -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredEncargados.setPredicate(encargado -> {
            if (encargado == null) {
                return false;
            }
            boolean matchesNombre = encargado.getNombre() != null && encargado.getNombre().toLowerCase().contains(normalized);
            return matchesNombre;
        });
    }

    private boolean isFormValid() {
        return nombreField != null && !nombreField.getText().isBlank();
    }

    private void refreshTable() {
        encargados.setAll(encargadoService.findAll());
        encargadoTable.refresh();
        onBuscar();
    }

    private void fillForm(Encargado encargado) {
        if (encargado == null) {
            clearForm();
            return;
        }
        nombreField.setText(encargado.getNombre());
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

