package com.example.hangar.ui.controller;

import com.example.hangar.model.Nave;
import com.example.hangar.service.NaveService;
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
public class NaveController {

    private final NaveService naveService;
    private final ObservableList<Nave> naves = FXCollections.observableArrayList();
    private final FilteredList<Nave> filteredNaves = new FilteredList<>(naves, nave -> true);

    public NaveController(NaveService naveService) {
        this.naveService = naveService;
    }

    @FXML
    private TableView<Nave> naveTable;

    @FXML
    private TableColumn<Nave, String> matriculaColumn;

    @FXML
    private TableColumn<Nave, String> estadoColumn;

    @FXML
    private TextField matriculaField;

    @FXML
    private TextField estadoField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (naveTable != null) {
            matriculaColumn.setCellValueFactory(new PropertyValueFactory<>("matricula"));
            estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
            naves.setAll(naveService.findAll());
            naveTable.setItems(filteredNaves);
            naveTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "La matrícula y el estado son obligatorios.");
            return;
        }
        Nave selected = naveTable.getSelectionModel().getSelectedItem();
        Nave nave = selected != null ? naveService.findById(selected.getId()) : new Nave();
        nave.setMatricula(matriculaField.getText().trim());
        nave.setEstado(estadoField.getText().trim());
        naveService.save(nave);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "La nave ha sido guardada correctamente.");
    }

    @FXML
    private void onEliminar() {
        Nave selected = naveTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir una nave para eliminarla.");
            return;
        }
        naveService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "La nave seleccionada fue eliminada.");
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (naveTable != null) {
            naveTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredNaves.setPredicate(nave -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredNaves.setPredicate(nave -> {
            if (nave == null) {
                return false;
            }
            boolean matchesMatricula = nave.getMatricula() != null && nave.getMatricula().toLowerCase().contains(normalized);
            boolean matchesEstado = nave.getEstado() != null && nave.getEstado().toLowerCase().contains(normalized);
            return matchesMatricula || matchesEstado;
        });
    }

    private boolean isFormValid() {
        return matriculaField != null && !matriculaField.getText().isBlank()
                && estadoField != null && !estadoField.getText().isBlank();
    }

    private void refreshTable() {
        naves.setAll(naveService.findAll());
        naveTable.refresh();
        onBuscar();
    }

    private void fillForm(Nave nave) {
        if (nave == null) {
            clearForm();
            return;
        }
        matriculaField.setText(nave.getMatricula());
        estadoField.setText(nave.getEstado());
    }

    private void clearForm() {
        if (matriculaField != null) {
            matriculaField.clear();
        }
        if (estadoField != null) {
            estadoField.clear();
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
