package com.example.hangar.ui.controller;

import com.example.hangar.model.Taller;
import com.example.hangar.service.TallerService;
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
public class TallerController {

    private final TallerService tallerService;
    private final ObservableList<Taller> talleres = FXCollections.observableArrayList();
    private final FilteredList<Taller> filteredTalleres = new FilteredList<>(talleres, taller -> true);

    public TallerController(TallerService tallerService) {
        this.tallerService = tallerService;
    }

    @FXML
    private TableView<Taller> tallerTable;

    @FXML
    private TableColumn<Taller, String> nombreColumn;

    @FXML
    private TableColumn<Taller, String> especialidadColumn;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField especialidadField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (tallerTable != null) {
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            especialidadColumn.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
            talleres.setAll(tallerService.findAll());
            tallerTable.setItems(filteredTalleres);
            tallerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "El nombre del taller es obligatorio.");
            return;
        }
        Taller selected = tallerTable.getSelectionModel().getSelectedItem();
        Taller taller = selected != null ? tallerService.findById(selected.getId()) : new Taller();
        taller.setNombre(nombreField.getText().trim());
        taller.setEspecialidad(especialidadField.getText() != null ? especialidadField.getText().trim() : null);
        tallerService.save(taller);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "El taller ha sido guardado correctamente.");
    }

    @FXML
    private void onEliminar() {
        Taller selected = tallerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un taller para eliminarlo.");
            return;
        }
        tallerService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El taller seleccionado fue eliminado.");
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (tallerTable != null) {
            tallerTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredTalleres.setPredicate(taller -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredTalleres.setPredicate(taller -> {
            if (taller == null) {
                return false;
            }
            boolean matchNombre = taller.getNombre() != null && taller.getNombre().toLowerCase().contains(normalized);
            boolean matchEspecialidad = taller.getEspecialidad() != null && taller.getEspecialidad().toLowerCase().contains(normalized);
            return matchNombre || matchEspecialidad;
        });
    }

    private boolean isFormValid() {
        return nombreField != null && !nombreField.getText().isBlank();
    }

    private void refreshTable() {
        talleres.setAll(tallerService.findAll());
        tallerTable.refresh();
        onBuscar();
    }

    private void fillForm(Taller taller) {
        if (taller == null) {
            clearForm();
            return;
        }
        nombreField.setText(taller.getNombre());
        especialidadField.setText(taller.getEspecialidad());
    }

    private void clearForm() {
        if (nombreField != null) {
            nombreField.clear();
        }
        if (especialidadField != null) {
            especialidadField.clear();
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
