package com.example.hangar.ui.controller;

import com.example.hangar.model.Empresa;
import com.example.hangar.service.EmpresaService;
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
public class EmpresaController {

    private final EmpresaService empresaService;
    private final ObservableList<Empresa> empresas = FXCollections.observableArrayList();
    private final FilteredList<Empresa> filteredEmpresas = new FilteredList<>(empresas, empresa -> true);

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @FXML
    private TableView<Empresa> empresaTable;

    @FXML
    private TableColumn<Empresa, String> nombreColumn;

    @FXML
    private TableColumn<Empresa, String> paisColumn;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField paisField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (empresaTable != null) {
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            paisColumn.setCellValueFactory(new PropertyValueFactory<>("pais"));
            empresas.setAll(empresaService.findAll());
            empresaTable.setItems(filteredEmpresas);
            empresaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Nombre y país son obligatorios.");
            return;
        }
        Empresa selected = empresaTable.getSelectionModel().getSelectedItem();
        Empresa empresa = selected != null ? empresaService.findById(selected.getId()) : new Empresa();
        empresa.setNombre(nombreField.getText().trim());
        empresa.setPais(paisField.getText().trim());
        empresaService.save(empresa);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "La empresa ha sido guardada correctamente.");
    }

    @FXML
    private void onEliminar() {
        Empresa selected = empresaTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir una empresa para eliminarla.");
            return;
        }
        empresaService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "La empresa seleccionada fue eliminada.");
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (empresaTable != null) {
            empresaTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredEmpresas.setPredicate(empresa -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredEmpresas.setPredicate(empresa -> {
            if (empresa == null) {
                return false;
            }
            boolean matchesNombre = empresa.getNombre() != null && empresa.getNombre().toLowerCase().contains(normalized);
            boolean matchesPais = empresa.getPais() != null && empresa.getPais().toLowerCase().contains(normalized);
            return matchesNombre || matchesPais;
        });
    }

    private boolean isFormValid() {
        return nombreField != null && !nombreField.getText().isBlank()
                && paisField != null && !paisField.getText().isBlank();
    }

    private void refreshTable() {
        empresas.setAll(empresaService.findAll());
        empresaTable.refresh();
        onBuscar();
    }

    private void fillForm(Empresa empresa) {
        if (empresa == null) {
            clearForm();
            return;
        }
        nombreField.setText(empresa.getNombre());
        paisField.setText(empresa.getPais());
    }

    private void clearForm() {
        if (nombreField != null) {
            nombreField.clear();
        }
        if (paisField != null) {
            paisField.clear();
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
