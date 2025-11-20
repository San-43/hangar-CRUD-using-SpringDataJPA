package com.example.hangar.ui.controller;

import com.example.hangar.model.Empresa;
import com.example.hangar.model.Hangar;
import com.example.hangar.model.Modelo;
import com.example.hangar.model.Nave;
import com.example.hangar.service.EmpresaService;
import com.example.hangar.service.HangarService;
import com.example.hangar.service.ModeloService;
import com.example.hangar.service.NaveService;
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
import java.util.function.Function;

@Component
public class NaveController {

    private final NaveService naveService;
    private final ModeloService modeloService;
    private final EmpresaService empresaService;
    private final HangarService hangarService;
    private final ObservableList<Nave> naves = FXCollections.observableArrayList();
    private final FilteredList<Nave> filteredNaves = new FilteredList<>(naves, nave -> true);
    private final ObservableList<Modelo> modelos = FXCollections.observableArrayList();
    private final ObservableList<Empresa> empresas = FXCollections.observableArrayList();
    private final ObservableList<Hangar> hangares = FXCollections.observableArrayList();

    public NaveController(NaveService naveService,
                          ModeloService modeloService,
                          EmpresaService empresaService,
                          HangarService hangarService) {
        this.naveService = naveService;
        this.modeloService = modeloService;
        this.empresaService = empresaService;
        this.hangarService = hangarService;
    }

    @FXML
    private TableView<Nave> naveTable;

    @FXML
    private TableColumn<Nave, Long> idColumn;

    @FXML
    private TableColumn<Nave, String> matriculaColumn;

    @FXML
    private TableColumn<Nave, String> estadoColumn;

    @FXML
    private TableColumn<Nave, String> modeloColumn;

    @FXML
    private TableColumn<Nave, String> empresaColumn;

    @FXML
    private TableColumn<Nave, String> hangarColumn;

    @FXML
    private TextField estadoField;

    @FXML

    @FXML
    private ComboBox<Modelo> modeloCombo;

    @FXML
    private ComboBox<Empresa> empresaCombo;

    @FXML
    private ComboBox<Hangar> hangarCombo;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (naveTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            matriculaColumn.setCellValueFactory(new PropertyValueFactory<>("matricula"));
            estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
            modeloColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getModelo() != null ? cellData.getValue().getModelo().getNombre() : ""));
            empresaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getEmpresa() != null ? cellData.getValue().getEmpresa().getNombre() : ""));
            hangarColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getHangar() != null ? cellData.getValue().getHangar().getCodigo() : ""));
            naves.setAll(naveService.findAll());
            naveTable.setItems(filteredNaves);
            naveTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }

        populateComboBox(modeloCombo, modelos, modeloService.findAll(), Modelo::getNombre);
        populateComboBox(empresaCombo, empresas, empresaService.findAll(), Empresa::getNombre);
        populateComboBox(hangarCombo, hangares, hangarService.findAll(), Hangar::getCodigo);
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Todos los campos son obligatorios.");
            return;
        }
        Nave selected = naveTable.getSelectionModel().getSelectedItem();
        Nave nave = selected != null ? naveService.findById(selected.getId()) : new Nave();
        nave.setEstado(estadoField != null ? estadoField.getText().trim() : null);
        nave.setEstado(estadoField.getText().trim());
        nave.setModelo(modeloCombo.getValue());
        nave.setEmpresa(empresaCombo.getValue());
        nave.setHangar(hangarCombo.getValue());
        naveService.save(nave);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "La nave ha sido guardada correctamente.");
    }

    @FXML
    private void onEliminar() {
        Nave selected = naveTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir una nave para eliminarla.");
            return;
        }
        naveService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "La nave seleccionada fue eliminada.");
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (naveTable != null) {
            naveTable.getSelectionModel().clearSelection();
        }
        if (modeloCombo != null) {
            modeloCombo.getSelectionModel().clearSelection();
        }
        if (empresaCombo != null) {
            empresaCombo.getSelectionModel().clearSelection();
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
            filteredNaves.setPredicate(nave -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredNaves.setPredicate(nave -> {
            if (nave == null) {
                return false;
            }
            boolean matchesMatricula = nave.getEstado() != null && nave.getEstado().toLowerCase().contains(normalized);
            boolean matchesEstado = nave.getEstado() != null && nave.getEstado().toLowerCase().contains(normalized);
            return matchesMatricula || matchesEstado;
        });
    }

    private boolean isFormValid() {
        return estadoField != null && !estadoField.getText().isBlank()
                && estadoField != null && !estadoField.getText().isBlank()
                && modeloCombo != null && modeloCombo.getValue() != null
                && empresaCombo != null && empresaCombo.getValue() != null
                && hangarCombo != null && hangarCombo.getValue() != null;
    }

    private void refreshTable() {
        naves.setAll(naveService.findAll());
        naveTable.refresh();
        onBuscar();
    }

    private void fillForm(Nave nave) {
        if (nave == null) {
            clearForm();
            return;
        }
        estadoField.setText(nave.getEstado());
        estadoField.setText(nave.getEstado());
        if (modeloCombo != null && nave.getModelo() != null) {
            Modelo modelo = modelos.stream()
                    .filter(m -> m.getId().equals(nave.getModelo().getId()))
                    .findFirst()
                    .orElse(null);
            modeloCombo.getSelectionModel().select(modelo);
        }
        if (empresaCombo != null && nave.getEmpresa() != null) {
            Empresa empresa = empresas.stream()
                    .filter(e -> e.getId().equals(nave.getEmpresa().getId()))
                    .findFirst()
                    .orElse(null);
            empresaCombo.getSelectionModel().select(empresa);
        }
        if (hangarCombo != null && nave.getHangar() != null) {
            Hangar hangar = hangares.stream()
                    .filter(h -> h.getId().equals(nave.getHangar().getId()))
                    .findFirst()
                    .orElse(null);
            hangarCombo.getSelectionModel().select(hangar);
        }
    }

    private void clearForm() {
        if (estadoField != null) {
            estadoField.clear();
        }
        if (estadoField != null) {
            estadoField.clear();
        }
        if (modeloCombo != null) {
            modeloCombo.getSelectionModel().clearSelection();
        }
        if (empresaCombo != null) {
            empresaCombo.getSelectionModel().clearSelection();
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

    private <T> void populateComboBox(ComboBox<T> comboBox,
                                      ObservableList<T> targetList,
                                      List<T> source,
                                      Function<T, String> labelProvider) {
        if (comboBox == null) {
            return;
        }
        targetList.setAll(source);
        comboBox.setItems(targetList);
        comboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : labelProvider.apply(item));
            }
        });
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : labelProvider.apply(item));
            }
        });
    }
}
