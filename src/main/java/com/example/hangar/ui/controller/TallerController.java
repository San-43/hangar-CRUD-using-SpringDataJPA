package com.example.hangar.ui.controller;

import com.example.hangar.model.Hangar;
import com.example.hangar.model.Taller;
import com.example.hangar.service.HangarService;
import com.example.hangar.service.TallerService;
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

import java.util.List;

@Component
public class TallerController {

    private final TallerService tallerService;
    private final HangarService hangarService;
    private final ObservableList<Taller> talleres = FXCollections.observableArrayList();
    private final FilteredList<Taller> filteredTalleres = new FilteredList<>(talleres, taller -> true);
    private final ObservableList<Hangar> hangares = FXCollections.observableArrayList();

    public TallerController(TallerService tallerService, HangarService hangarService) {
        this.tallerService = tallerService;
        this.hangarService = hangarService;
    }

    @FXML
    private TableView<Taller> tallerTable;

    @FXML
    private TableColumn<Taller, Long> idColumn;

    @FXML
    private TableColumn<Taller, String> nombreColumn;

    @FXML
    private TableColumn<Taller, String> especialidadColumn;

    @FXML
    private TableColumn<Taller, String> hangarColumn;

    @FXML
    private TableColumn<Taller, Number> reportesColumn;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField especialidadField;

    @FXML
    private ComboBox<Hangar> hangarCombo;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (tallerTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            especialidadColumn.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
            hangarColumn.setCellValueFactory(data -> new SimpleStringProperty(
                    data.getValue().getHangar() != null ? data.getValue().getHangar().getCodigo() : ""));
            reportesColumn.setCellValueFactory(data -> new SimpleIntegerProperty(
                    data.getValue().getReportes() != null ? data.getValue().getReportes().size() : 0));
            talleres.setAll(tallerService.findAll());
            tallerTable.setItems(filteredTalleres);
            tallerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }

        populateHangarCombo();
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "El nombre del taller es obligatorio.");
            return;
        }
        Taller selected = tallerTable.getSelectionModel().getSelectedItem();
        Taller taller = selected != null ? tallerService.findById(selected.getId()) : new Taller();
        // nombre and especialidad fields removed from Taller entity
        taller.setHangar(hangarCombo != null ? hangarCombo.getValue() : null);
        // encargado would need to be set from UI, but UI fields don't exist yet
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
        if (hangarCombo != null) {
            hangarCombo.getSelectionModel().clearSelection();
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
            // Taller no longer has nombre/especialidad - search by ID and related entities
            String idStr = taller.getId() != null ? taller.getId().toString() : "";
            boolean matchId = idStr.contains(normalized);
            boolean matchHangar = taller.getHangar() != null && 
                                  taller.getHangar().getDescripcion() != null && 
                                  taller.getHangar().getDescripcion().toLowerCase().contains(normalized);
            return matchId || matchHangar;
        });
    }

    private boolean isFormValid() {
        return nombreField != null && !nombreField.getText().isBlank()
                && hangarCombo != null && hangarCombo.getValue() != null;
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
        nombreField.setText(taller.getId() != null ? taller.getId().toString() : "");
        especialidadField.setText("");
        if (hangarCombo != null && taller.getHangar() != null) {
            Hangar hangar = hangares.stream()
                    .filter(h -> h.getId().equals(taller.getHangar().getId()))
                    .findFirst()
                    .orElse(null);
            hangarCombo.getSelectionModel().select(hangar);
        }
    }

    private void clearForm() {
        if (nombreField != null) {
            nombreField.clear();
        }
        if (especialidadField != null) {
            especialidadField.clear();
        }
        if (hangarCombo != null) {
            hangarCombo.getSelectionModel().clearSelection();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void populateHangarCombo() {
        if (hangarCombo == null) {
            return;
        }
        List<Hangar> allHangares = hangarService.findAll();
        hangares.setAll(allHangares);
        hangarCombo.setItems(hangares);
        hangarCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Hangar item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCodigo());
            }
        });
        hangarCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Hangar item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCodigo());
            }
        });
    }
}
