package com.example.hangar.ui.controller;

import com.example.hangar.model.Modelo;
import com.example.hangar.service.ModeloService;
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
public class ModeloController {

    private final ModeloService modeloService;
    private final ObservableList<Modelo> modelos = FXCollections.observableArrayList();
    private final FilteredList<Modelo> filteredModelos = new FilteredList<>(modelos, modelo -> true);

    public ModeloController(ModeloService modeloService) {
        this.modeloService = modeloService;
    }

    @FXML
    private TableView<Modelo> modeloTable;

    @FXML
    private TableColumn<Modelo, Long> idColumn;

    @FXML
    private TableColumn<Modelo, String> nombreColumn;

    @FXML
    private TableColumn<Modelo, String> fabricanteColumn;

    @FXML
    private TableColumn<Modelo, Integer> capacidadColumn;

    @FXML
    private TableColumn<Modelo, Number> navesColumn;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField fabricanteField;

    @FXML
    private TextField capacidadField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (modeloTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            fabricanteColumn.setCellValueFactory(new PropertyValueFactory<>("fabricante"));
            capacidadColumn.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
            navesColumn.setCellValueFactory(cellData ->
                    new SimpleIntegerProperty(cellData.getValue().getNaves().size()));
            modelos.setAll(modeloService.findAll());
            modeloTable.setItems(filteredModelos);
            modeloTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Nombre es obligatorio.");
            return;
        }
        Modelo selected = modeloTable.getSelectionModel().getSelectedItem();
        Modelo modelo = selected != null ? modeloService.findById(selected.getId()) : new Modelo();
        modelo.setNombre(nombreField.getText().trim());
        modelo.setFabricante(fabricanteField.getText().trim());

        // Validar y establecer capacidad
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

        modeloService.save(modelo);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "El modelo ha sido guardado correctamente.");
    }

    @FXML
    private void onEliminar() {
        Modelo selected = modeloTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un modelo para eliminarlo.");
            return;
        }

        // Validar si el modelo tiene naves asociadas
        if (selected.getNaves() != null && !selected.getNaves().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No se puede eliminar",
                    "Este modelo tiene " + selected.getNaves().size() + " nave(s) asociada(s). " +
                    "Primero debe eliminar o reasignar las naves relacionadas.");
            return;
        }

        try {
            modeloService.delete(selected.getId());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El modelo seleccionado fue eliminado.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar este modelo porque tiene naves asociadas. " +
                    "Primero debe eliminar o reasignar las naves relacionadas.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar el modelo: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (modeloTable != null) {
            modeloTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredModelos.setPredicate(modelo -> true);
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
        filteredModelos.setPredicate(modelo -> {
            if (modelo == null) {
                return false;
            }
            boolean matchesNombre = modelo.getNombre() != null && modelo.getNombre().toLowerCase().contains(normalized);
            boolean matchesFabricante = modelo.getFabricante() != null && modelo.getFabricante().toLowerCase().contains(normalized);
            boolean matchesCapacidad = finalNumericTerm != null && modelo.getCapacidad() != null && modelo.getCapacidad().equals(finalNumericTerm);
            boolean matchesNaves = finalNumericTerm != null && modelo.getNaves().size() == finalNumericTerm;
            return matchesNombre || matchesFabricante || matchesCapacidad || matchesNaves;
        });
    }

    private boolean isFormValid() {
        return nombreField != null && !nombreField.getText().isBlank();
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
        nombreField.setText(modelo.getNombre());
        fabricanteField.setText(modelo.getFabricante());
        capacidadField.setText(modelo.getCapacidad() != null ? modelo.getCapacidad().toString() : "");
    }

    private void clearForm() {
        if (nombreField != null) {
            nombreField.clear();
        }
        if (fabricanteField != null) {
            fabricanteField.clear();
        }
        if (capacidadField != null) {
            capacidadField.clear();
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

