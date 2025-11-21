package com.example.hangar.ui.controller;

import com.example.hangar.model.Encargado;
import com.example.hangar.model.Hangar;
import com.example.hangar.model.Taller;
import com.example.hangar.service.EncargadoService;
import com.example.hangar.service.HangarService;
import com.example.hangar.service.TallerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class TallerController {

    private final TallerService tallerService;
    private final HangarService hangarService;
    private final EncargadoService encargadoService;
    private final ObservableList<Taller> talleres = FXCollections.observableArrayList();
    private final FilteredList<Taller> filteredTalleres = new FilteredList<>(talleres, taller -> true);
    private final ObservableList<Hangar> hangares = FXCollections.observableArrayList();
    private final ObservableList<Encargado> encargados = FXCollections.observableArrayList();

    public TallerController(TallerService tallerService, HangarService hangarService, EncargadoService encargadoService) {
        this.tallerService = tallerService;
        this.hangarService = hangarService;
        this.encargadoService = encargadoService;
    }

    @FXML
    private TableView<Taller> tallerTable;

    @FXML
    private TableColumn<Taller, Integer> idColumn;

    @FXML
    private TableColumn<Taller, String> hangarColumn;

    @FXML
    private TableColumn<Taller, String> encargadoColumn;

    @FXML
    private ComboBox<Hangar> hangarCombo;

    @FXML
    private ComboBox<Encargado> encargadoCombo;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (tallerTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idTaller"));
            hangarColumn.setCellValueFactory(cellData -> {
                Hangar hangar = cellData.getValue().getHangar();
                return new SimpleStringProperty(hangar != null ? hangar.getDescripcion() : "");
            });
            encargadoColumn.setCellValueFactory(cellData -> {
                Encargado encargado = cellData.getValue().getEncargado();
                return new SimpleStringProperty(encargado != null ? encargado.getNombre() : "");
            });
            talleres.setAll(tallerService.findAll());
            tallerTable.setItems(filteredTalleres);
            tallerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
        populateHangares();
        populateEncargados();
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Seleccione un hangar y un encargado.");
            return;
        }
        Taller selected = tallerTable.getSelectionModel().getSelectedItem();
        Taller taller = selected != null ? tallerService.findById(selected.getIdTaller()) : new Taller();

        taller.setHangar(hangarCombo.getValue());
        taller.setEncargado(encargadoCombo.getValue());

        try {
            tallerService.save(taller);
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "El taller ha sido guardado correctamente.");
        } catch (IllegalArgumentException e) {
            // Capturar errores de validación de negocio
            showAlert(Alert.AlertType.ERROR, "Error de validación", e.getMessage());
        } catch (Exception e) {
            // Capturar cualquier otro error
            showAlert(Alert.AlertType.ERROR, "Error",
                "Ocurrió un error al guardar el taller: " + e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        Taller selected = tallerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un taller para eliminarlo.");
            return;
        }

        try {
            String constraintMessage = tallerService.checkDeletionConstraints(selected.getIdTaller());
            if (constraintMessage != null) {
                showAlert(Alert.AlertType.WARNING, "No se puede eliminar", constraintMessage);
                return;
            }

            tallerService.delete(selected.getIdTaller());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El taller seleccionado fue eliminado.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar este taller porque tiene reportes u otros registros asociados.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar el taller: " + e.getMessage());
        }
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
            boolean matchesHangar = taller.getHangar() != null && taller.getHangar().getDescripcion() != null
                    && taller.getHangar().getDescripcion().toLowerCase().contains(normalized);
            boolean matchesEncargado = taller.getEncargado() != null && taller.getEncargado().getNombre() != null
                    && taller.getEncargado().getNombre().toLowerCase().contains(normalized);
            return matchesHangar || matchesEncargado;
        });
    }

    private void populateHangares() {
        hangares.setAll(hangarService.findAll());
        hangares.sort(Comparator.comparing(h -> h.getDescripcion() != null ? h.getDescripcion() : "", String.CASE_INSENSITIVE_ORDER));
        if (hangarCombo == null) return;
        hangarCombo.setItems(hangares);
        hangarCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Hangar item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDescripcion());
            }
        });
        hangarCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Hangar item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDescripcion());
            }
        });
    }

    private void populateEncargados() {
        encargados.setAll(encargadoService.findAll());
        encargados.sort(Comparator.comparing(e -> e.getNombre() != null ? e.getNombre() : "", String.CASE_INSENSITIVE_ORDER));
        if (encargadoCombo == null) return;
        encargadoCombo.setItems(encargados);
        encargadoCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Encargado item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        encargadoCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Encargado item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
    }

    private boolean isFormValid() {
        return hangarCombo != null && hangarCombo.getValue() != null
                && encargadoCombo != null && encargadoCombo.getValue() != null;
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
        if (taller.getHangar() != null) {
            hangarCombo.setValue(hangares.stream()
                    .filter(h -> h.getIdHangar().equals(taller.getHangar().getIdHangar()))
                    .findFirst()
                    .orElse(null));
        }
        if (taller.getEncargado() != null) {
            encargadoCombo.setValue(encargados.stream()
                    .filter(e -> e.getIdEncargado().equals(taller.getEncargado().getIdEncargado()))
                    .findFirst()
                    .orElse(null));
        }
    }

    private void clearForm() {
        if (hangarCombo != null) hangarCombo.setValue(null);
        if (encargadoCombo != null) encargadoCombo.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

