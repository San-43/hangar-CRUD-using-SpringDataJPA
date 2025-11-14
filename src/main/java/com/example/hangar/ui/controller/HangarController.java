package com.example.hangar.ui.controller;

import com.example.hangar.model.Empresa;
import com.example.hangar.model.Hangar;
import com.example.hangar.service.EmpresaService;
import com.example.hangar.service.HangarService;
import javafx.beans.property.SimpleIntegerProperty;
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
public class HangarController {

    private final HangarService hangarService;
    private final EmpresaService empresaService;

    private final ObservableList<Hangar> hangares = FXCollections.observableArrayList();
    private final FilteredList<Hangar> filteredHangares = new FilteredList<>(hangares, hangar -> true);
    private final ObservableList<Empresa> empresas = FXCollections.observableArrayList();

    public HangarController(HangarService hangarService, EmpresaService empresaService) {
        this.hangarService = hangarService;
        this.empresaService = empresaService;
    }

    @FXML
    private TableView<Hangar> hangarTable;

    @FXML
    private TableColumn<Hangar, Long> idColumn;

    @FXML
    private TableColumn<Hangar, String> codigoColumn;

    @FXML
    private TableColumn<Hangar, Number> capacidadColumn;

    @FXML
    private TableColumn<Hangar, String> ubicacionColumn;

    @FXML
    private TableColumn<Hangar, String> empresaColumn;

    @FXML
    private TextField codigoField;

    @FXML
    private TextField capacidadField;

    @FXML
    private TextField ubicacionField;

    @FXML
    private ComboBox<Empresa> empresaCombo;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (hangarTable == null) {
            return;
        }
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        capacidadColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(
                cellData.getValue().getCapacidad() != null ? cellData.getValue().getCapacidad() : 0));
        ubicacionColumn.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        empresaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getEmpresa() != null ? cellData.getValue().getEmpresa().getNombre() : ""));

        populateEmpresas();
        refreshTable();
        hangarTable.setItems(filteredHangares);
        hangarTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos",
                    "Código, capacidad (numérica) y empresa son obligatorios.");
            return;
        }
        Hangar selected = hangarTable.getSelectionModel().getSelectedItem();
        Hangar hangar = selected != null ? hangarService.findById(selected.getId()) : new Hangar();
        hangar.setCodigo(codigoField.getText().trim());
        hangar.setCapacidad(Integer.parseInt(capacidadField.getText().trim()));
        hangar.setUbicacion(ubicacionField.getText() != null ? ubicacionField.getText().trim() : null);
        hangar.setEmpresa(empresaCombo.getValue());
        hangarService.save(hangar);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Hangar guardado", "El hangar se guardó correctamente.");
    }

    @FXML
    private void onEliminar() {
        Hangar selected = hangarTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro",
                    "Debe seleccionar un hangar para eliminarlo.");
            return;
        }

        // Validar si el hangar tiene registros asociados
        try {
            String constraintMessage = hangarService.checkDeletionConstraints(selected.getId());

            if (constraintMessage != null) {
                showAlert(Alert.AlertType.WARNING, "No se puede eliminar", constraintMessage);
                return;
            }

            hangarService.delete(selected.getId());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Hangar eliminado", "El hangar fue eliminado correctamente.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar este hangar porque tiene registros asociados. " +
                    "Primero debe eliminar o reasignar los registros relacionados.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar el hangar: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        if (hangarTable != null) {
            hangarTable.getSelectionModel().clearSelection();
        }
        clearForm();
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredHangares.setPredicate(h -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        Integer numericTerm = null;
        try {
            numericTerm = Integer.parseInt(term.trim());
        } catch (NumberFormatException ignored) {
        }
        Integer finalNumericTerm = numericTerm;
        filteredHangares.setPredicate(hangar -> {
            if (hangar == null) {
                return false;
            }
            boolean matchesCodigo = hangar.getCodigo() != null && hangar.getCodigo().toLowerCase().contains(normalized);
            boolean matchesUbicacion = hangar.getUbicacion() != null && hangar.getUbicacion().toLowerCase().contains(normalized);
            boolean matchesCapacidad = finalNumericTerm != null && hangar.getCapacidad() != null
                    && hangar.getCapacidad().equals(finalNumericTerm);
            boolean matchesEmpresa = hangar.getEmpresa() != null && hangar.getEmpresa().getNombre() != null
                    && hangar.getEmpresa().getNombre().toLowerCase().contains(normalized);
            return matchesCodigo || matchesUbicacion || matchesCapacidad || matchesEmpresa;
        });
    }

    private void populateEmpresas() {
        empresas.setAll(empresaService.findAll());
        if (empresaCombo == null) {
            return;
        }
        empresaCombo.setItems(empresas);
        empresaCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Empresa item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        empresaCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Empresa item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
    }

    private boolean isFormValid() {
        if (codigoField == null || codigoField.getText().isBlank()) {
            return false;
        }
        if (capacidadField == null || capacidadField.getText().isBlank()) {
            return false;
        }
        try {
            Integer.parseInt(capacidadField.getText().trim());
        } catch (NumberFormatException ex) {
            return false;
        }
        return empresaCombo != null && empresaCombo.getValue() != null;
    }

    private void refreshTable() {
        hangares.setAll(hangarService.findAll());
        if (hangarTable != null) {
            hangarTable.refresh();
        }
        onBuscar();
    }

    private void fillForm(Hangar hangar) {
        if (hangar == null) {
            clearForm();
            return;
        }
        if (codigoField != null) {
            codigoField.setText(hangar.getCodigo());
        }
        if (capacidadField != null) {
            capacidadField.setText(hangar.getCapacidad() != null ? String.valueOf(hangar.getCapacidad()) : "");
        }
        if (ubicacionField != null) {
            ubicacionField.setText(hangar.getUbicacion());
        }
        if (empresaCombo != null && hangar.getEmpresa() != null) {
            Empresa empresa = empresas.stream()
                    .filter(e -> e.getId().equals(hangar.getEmpresa().getId()))
                    .findFirst()
                    .orElse(null);
            empresaCombo.getSelectionModel().select(empresa);
        }
    }

    private void clearForm() {
        if (codigoField != null) {
            codigoField.clear();
        }
        if (capacidadField != null) {
            capacidadField.clear();
        }
        if (ubicacionField != null) {
            ubicacionField.clear();
        }
        if (empresaCombo != null) {
            empresaCombo.getSelectionModel().clearSelection();
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
