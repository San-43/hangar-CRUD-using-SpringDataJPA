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
    private TextField descripcionField;

    @FXML
    private TextField capacidadField;

    @FXML
    private TextField areaField;

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
        if (ubicacionColumn != null) ubicacionColumn.setCellValueFactory(new PropertyValueFactory<>("area"));
        if (empresaColumn != null) {
            empresaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(""));
        }

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
        hangar.setDescripcion(descripcionField.getText().trim());
        hangar.setCapacidad(Integer.parseInt(capacidadField.getText().trim()));
        hangar.setArea(areaField.getText() != null ? areaField.getText().trim() : null);
        // Hangar no longer has empresa relationship
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
        hangarService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Hangar eliminado", "El hangar fue eliminado correctamente.");
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
            boolean matchesCodigo = hangar.getDescripcion() != null && hangar.getDescripcion().toLowerCase().contains(normalized);
            boolean matchesUbicacion = hangar.getArea() != null && hangar.getArea().toLowerCase().contains(normalized);
            boolean matchesCapacidad = finalNumericTerm != null && hangar.getCapacidad() != null
                    && hangar.getCapacidad().equals(finalNumericTerm);
            // Empresa relationship removed
            return matchesCodigo || matchesUbicacion || matchesCapacidad;
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
        if (descripcionField == null || descripcionField.getText().isBlank()) {
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
        if (descripcionField != null) {
            descripcionField.setText(hangar.getDescripcion());
        }
        if (capacidadField != null) {
            capacidadField.setText(hangar.getCapacidad() != null ? String.valueOf(hangar.getCapacidad()) : "");
        }
        if (areaField != null) {
            areaField.setText(hangar.getArea());
        }
        if (empresaCombo != null && hangar.getNaves().isEmpty() ? null : null != null) {
            Empresa empresa = empresas.stream()
                    .filter(e -> e.getId().equals(hangar.getNaves().isEmpty() ? null : null.getId()))
                    .findFirst()
                    .orElse(null);
            empresaCombo.getSelectionModel().select(empresa);
        }
    }

    private void clearForm() {
        if (descripcionField != null) {
            descripcionField.clear();
        }
        if (capacidadField != null) {
            capacidadField.clear();
        }
        if (areaField != null) {
            areaField.clear();
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
