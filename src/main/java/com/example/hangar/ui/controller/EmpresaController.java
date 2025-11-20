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
import javafx.beans.property.SimpleIntegerProperty;
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
    private TableColumn<Empresa, Integer> idColumn;

    @FXML
    private TableColumn<Empresa, String> nombreColumn;

    @FXML
    private TableColumn<Empresa, String> contactoColumn;

    @FXML
    private TableColumn<Empresa, String> ubicacionColumn;

    @FXML
    private TableColumn<Empresa, String> rfcColumn;

    @FXML
    private TableColumn<Empresa, Number> navesColumn;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField contactoField;

    @FXML
    private TextField ubicacionField;

    @FXML
    private TextField rfcField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (empresaTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            if (contactoColumn != null) contactoColumn.setCellValueFactory(new PropertyValueFactory<>("contacto"));
            if (ubicacionColumn != null) ubicacionColumn.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
            if (rfcColumn != null) rfcColumn.setCellValueFactory(new PropertyValueFactory<>("rfc"));
            if (navesColumn != null) {
                navesColumn.setCellValueFactory(cellData ->
                        new SimpleIntegerProperty(cellData.getValue().getNaves().size()));
            }
            empresas.setAll(empresaService.findAll());
            empresaTable.setItems(filteredEmpresas);
            empresaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "El nombre es obligatorio.");
            return;
        }
        Empresa selected = empresaTable.getSelectionModel().getSelectedItem();
        Empresa empresa = selected != null ? empresaService.findById(selected.getId()) : new Empresa();
        empresa.setNombre(nombreField.getText().trim());
        if (contactoField != null) empresa.setContacto(contactoField.getText().trim());
        if (ubicacionField != null) empresa.setUbicacion(ubicacionField.getText().trim());
        if (rfcField != null) empresa.setRfc(rfcField.getText().trim());
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
        Integer numericTerm = null;
        try {
            numericTerm = Integer.parseInt(term.trim());
        } catch (NumberFormatException ignored) {
            // El término de búsqueda no es numérico.
        }
        Integer finalNumericTerm = numericTerm;
        filteredEmpresas.setPredicate(empresa -> {
            if (empresa == null) {
                return false;
            }
            boolean matchesNombre = empresa.getNombre() != null && empresa.getNombre().toLowerCase().contains(normalized);
            boolean matchesContacto = empresa.getContacto() != null && empresa.getContacto().toLowerCase().contains(normalized);
            boolean matchesUbicacion = empresa.getUbicacion() != null && empresa.getUbicacion().toLowerCase().contains(normalized);
            boolean matchesRfc = empresa.getRfc() != null && empresa.getRfc().toLowerCase().contains(normalized);
            boolean matchesNaves = finalNumericTerm != null && empresa.getNaves().size() == finalNumericTerm;
            return matchesNombre || matchesContacto || matchesUbicacion || matchesRfc || matchesNaves;
        });
    }

    private boolean isFormValid() {
        return nombreField != null && !nombreField.getText().isBlank();
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
        if (contactoField != null && empresa.getContacto() != null) contactoField.setText(empresa.getContacto());
        if (ubicacionField != null && empresa.getUbicacion() != null) ubicacionField.setText(empresa.getUbicacion());
        if (rfcField != null && empresa.getRfc() != null) rfcField.setText(empresa.getRfc());
    }

    private void clearForm() {
        if (nombreField != null) {
            nombreField.clear();
        }
        if (contactoField != null) {
            contactoField.clear();
        }
        if (ubicacionField != null) {
            ubicacionField.clear();
        }
        if (rfcField != null) {
            rfcField.clear();
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
