package com.example.hangar.ui.controller;

import com.example.hangar.model.Hangar;
import com.example.hangar.service.HangarService;
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
public class HangarController {

    private final HangarService hangarService;
    private final ObservableList<Hangar> hangares = FXCollections.observableArrayList();
    private final FilteredList<Hangar> filteredHangares = new FilteredList<>(hangares, hangar -> true);

    public HangarController(HangarService hangarService) {
        this.hangarService = hangarService;
    }

    @FXML
    private TableView<Hangar> hangarTable;

    @FXML
    private TableColumn<Hangar, Integer> idColumn;

    @FXML
    private TableColumn<Hangar, String> descripcionColumn;

    @FXML
    private TableColumn<Hangar, Integer> capacidadColumn;

    @FXML
    private TableColumn<Hangar, String> areaColumn;

    @FXML
    private TableColumn<Hangar, Integer> numColumn;

    @FXML
    private TextField descripcionField;

    @FXML
    private TextField capacidadField;

    @FXML
    private TextField areaField;

    @FXML
    private TextField numField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (hangarTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idHangar"));
            descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            capacidadColumn.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
            areaColumn.setCellValueFactory(new PropertyValueFactory<>("area"));
            numColumn.setCellValueFactory(new PropertyValueFactory<>("num"));
            hangares.setAll(hangarService.findAll());
            hangarTable.setItems(filteredHangares);
            hangarTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "La descripción es obligatoria.");
            return;
        }
        Hangar selected = hangarTable.getSelectionModel().getSelectedItem();
        Hangar hangar = selected != null ? hangarService.findById(selected.getIdHangar()) : new Hangar();

        hangar.setDescripcion(descripcionField.getText().trim());
        hangar.setArea(areaField.getText().trim());

        // Validar y establecer capacidad
        String capacidadText = capacidadField.getText().trim();
        if (!capacidadText.isEmpty()) {
            try {
                hangar.setCapacidad(Integer.parseInt(capacidadText));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Formato incorrecto", "La capacidad debe ser un número entero.");
                return;
            }
        } else {
            hangar.setCapacidad(null);
        }

        // Validar y establecer num
        String numText = numField.getText().trim();
        if (!numText.isEmpty()) {
            try {
                hangar.setNum(Integer.parseInt(numText));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Formato incorrecto", "El número debe ser un entero.");
                return;
            }
        } else {
            hangar.setNum(null);
        }

        hangarService.save(hangar);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "El hangar ha sido guardado correctamente.");
    }

    @FXML
    private void onEliminar() {
        Hangar selected = hangarTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un hangar para eliminarlo.");
            return;
        }

        try {
            String constraintMessage = hangarService.checkDeletionConstraints(selected.getIdHangar());
            if (constraintMessage != null) {
                showAlert(Alert.AlertType.WARNING, "No se puede eliminar", constraintMessage);
                return;
            }

            hangarService.delete(selected.getIdHangar());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El hangar seleccionado fue eliminado.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar este hangar porque tiene naves, talleres u otros registros asociados.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar el hangar: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (hangarTable != null) {
            hangarTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredHangares.setPredicate(hangar -> true);
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
            boolean matchesDescripcion = hangar.getDescripcion() != null && hangar.getDescripcion().toLowerCase().contains(normalized);
            boolean matchesArea = hangar.getArea() != null && hangar.getArea().toLowerCase().contains(normalized);
            boolean matchesCapacidad = finalNumericTerm != null && hangar.getCapacidad() != null && hangar.getCapacidad().equals(finalNumericTerm);
            boolean matchesNum = finalNumericTerm != null && hangar.getNum() != null && hangar.getNum().equals(finalNumericTerm);
            return matchesDescripcion || matchesArea || matchesCapacidad || matchesNum;
        });
    }

    private boolean isFormValid() {
        return descripcionField != null && !descripcionField.getText().isBlank();
    }

    private void refreshTable() {
        hangares.setAll(hangarService.findAll());
        hangarTable.refresh();
        onBuscar();
    }

    private void fillForm(Hangar hangar) {
        if (hangar == null) {
            clearForm();
            return;
        }
        descripcionField.setText(hangar.getDescripcion());
        areaField.setText(hangar.getArea());
        capacidadField.setText(hangar.getCapacidad() != null ? hangar.getCapacidad().toString() : "");
        numField.setText(hangar.getNum() != null ? hangar.getNum().toString() : "");
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
        if (numField != null) {
            numField.clear();
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

