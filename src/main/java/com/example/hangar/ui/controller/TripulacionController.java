package com.example.hangar.ui.controller;

import com.example.hangar.model.Tripulacion;
import com.example.hangar.service.TripulacionService;
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
public class TripulacionController {

    private final TripulacionService tripulacionService;
    private final ObservableList<Tripulacion> tripulaciones = FXCollections.observableArrayList();
    private final FilteredList<Tripulacion> filteredTripulaciones = new FilteredList<>(tripulaciones, tripulacion -> true);

    public TripulacionController(TripulacionService tripulacionService) {
        this.tripulacionService = tripulacionService;
    }

    @FXML
    private TableView<Tripulacion> tripulacionTable;

    @FXML
    private TableColumn<Tripulacion, String> nombreColumn;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (tripulacionTable != null) {
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            tripulaciones.setAll(tripulacionService.findAll());
            tripulacionTable.setItems(filteredTripulaciones);
            tripulacionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "El nombre es obligatorio.");
            return;
        }
        Tripulacion selected = tripulacionTable.getSelectionModel().getSelectedItem();
        Tripulacion tripulacion = selected != null ? tripulacionService.findById(selected.getId()) : new Tripulacion();
        tripulacion.setNombre(nombreField.getText().trim());
        tripulacionService.save(tripulacion);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "La tripulación ha sido guardada correctamente.");
    }

    @FXML
    private void onEliminar() {
        Tripulacion selected = tripulacionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir una tripulación para eliminarla.");
            return;
        }
        tripulacionService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "La tripulación seleccionada fue eliminada.");
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (tripulacionTable != null) {
            tripulacionTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredTripulaciones.setPredicate(tripulacion -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredTripulaciones.setPredicate(tripulacion -> tripulacion != null
                && tripulacion.getNombre() != null
                && tripulacion.getNombre().toLowerCase().contains(normalized));
    }

    private boolean isFormValid() {
        return nombreField != null && !nombreField.getText().isBlank();
    }

    private void refreshTable() {
        tripulaciones.setAll(tripulacionService.findAll());
        tripulacionTable.refresh();
        onBuscar();
    }

    private void fillForm(Tripulacion tripulacion) {
        if (tripulacion == null) {
            clearForm();
            return;
        }
        nombreField.setText(tripulacion.getNombre());
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
