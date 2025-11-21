package com.example.hangar.ui.controller;

import com.example.hangar.model.Modelo;
import com.example.hangar.service.ModeloService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component
public class ModeloController {
    private final ModeloService modeloService;
    private final ObservableList<Modelo> modelos = FXCollections.observableArrayList();
    private final FilteredList<Modelo> filteredModelos = new FilteredList<>(modelos, modelo -> true);

    public ModeloController(ModeloService modeloService) {
        this.modeloService = modeloService;
    }

    @FXML private TableView<Modelo> modeloTable;
    @FXML private TableColumn<Modelo, Integer> idColumn;
    @FXML private TableColumn<Modelo, String> nombreColumn;
    @FXML private TableColumn<Modelo, Integer> pesoColumn;
    @FXML private TableColumn<Modelo, Integer> capacidadColumn;
    @FXML private TableColumn<Modelo, String> paisFabricacionColumn;
    @FXML private TextField nombreField;
    @FXML private TextField pesoField;
    @FXML private TextField capacidadField;
    @FXML private TextField paisFabricacionField;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        if (modeloTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idModelo"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreModelo"));
            pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));
            capacidadColumn.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
            paisFabricacionColumn.setCellValueFactory(new PropertyValueFactory<>("paisFabricacion"));
            modelos.setAll(modeloService.findAll());
            modeloTable.setItems(filteredModelos);
            modeloTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "El nombre, peso y capacidad del modelo son obligatorios.");
            return;
        }
        Modelo selected = modeloTable.getSelectionModel().getSelectedItem();
        Modelo modelo = selected != null ? modeloService.findById(selected.getIdModelo()) : new Modelo();

        modelo.setNombreModelo(nombreField.getText().trim());

        String pesoText = pesoField.getText().trim();
        if (!pesoText.isEmpty()) {
            try {
                modelo.setPeso(Integer.parseInt(pesoText));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Formato incorrecto", "El peso debe ser un número entero.");
                return;
            }
        } else {
            modelo.setPeso(null);
        }

        String capacidadText = capacidadField.getText().trim();
        if (!capacidadText.isEmpty()) {
            try {
                modelo.setCapacidad(Integer.parseInt(capacidadText));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Formato incorrecto", "La capacidad debe ser un número entero.");
                return;
            }
        } else {
            modelo.setCapacidad(null);
        }

        modelo.setPaisFabricacion(paisFabricacionField.getText().trim());

        try {
            modeloService.save(modelo);
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "El modelo ha sido guardado correctamente.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error de validación", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al guardar el modelo: " + e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        Modelo selected = modeloTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un modelo para eliminarlo.");
            return;
        }
        try {
            modeloService.delete(selected.getIdModelo());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El modelo seleccionado fue eliminado.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar este modelo porque tiene naves, pilotos u otros registros asociados.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar el modelo: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (modeloTable != null) modeloTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) return;
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredModelos.setPredicate(m -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        Integer numericTerm = null;
        try {
            numericTerm = Integer.parseInt(term.trim());
        } catch (NumberFormatException ignored) {}
        Integer finalNumericTerm = numericTerm;
        filteredModelos.setPredicate(modelo -> {
            if (modelo == null) return false;
            boolean matchesNombre = modelo.getNombreModelo() != null && modelo.getNombreModelo().toLowerCase().contains(normalized);
            boolean matchesPais = modelo.getPaisFabricacion() != null && modelo.getPaisFabricacion().toLowerCase().contains(normalized);
            boolean matchesPeso = finalNumericTerm != null && modelo.getPeso() != null && modelo.getPeso().equals(finalNumericTerm);
            boolean matchesCapacidad = finalNumericTerm != null && modelo.getCapacidad() != null && modelo.getCapacidad().equals(finalNumericTerm);
            return matchesNombre || matchesPais || matchesPeso || matchesCapacidad;
        });
    }

    private boolean isFormValid() {
        return nombreField != null && !nombreField.getText().isBlank()
            && pesoField != null && !pesoField.getText().isBlank()
            && capacidadField != null && !capacidadField.getText().isBlank();
    }

    private void refreshTable() {
        modelos.setAll(modeloService.findAll());
        modeloTable.refresh();
        onBuscar();
    }

    private void fillForm(Modelo modelo) {
        if (modelo == null) {
            clearForm();
            return;
        }
        nombreField.setText(modelo.getNombreModelo());
        pesoField.setText(modelo.getPeso() != null ? modelo.getPeso().toString() : "");
        capacidadField.setText(modelo.getCapacidad() != null ? modelo.getCapacidad().toString() : "");
        paisFabricacionField.setText(modelo.getPaisFabricacion());
    }

    private void clearForm() {
        if (nombreField != null) nombreField.clear();
        if (pesoField != null) pesoField.clear();
        if (capacidadField != null) capacidadField.clear();
        if (paisFabricacionField != null) paisFabricacionField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

