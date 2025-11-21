package com.example.hangar.ui.controller;

import com.example.hangar.model.*;
import com.example.hangar.service.*;
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
public class NaveController {
    private final NaveService naveService;
    private final EmpresaService empresaService;
    private final HangarService hangarService;
    private final ModeloService modeloService;
    private final ObservableList<Nave> naves = FXCollections.observableArrayList();
    private final FilteredList<Nave> filteredNaves = new FilteredList<>(naves, nave -> true);
    private final ObservableList<Empresa> empresas = FXCollections.observableArrayList();
    private final ObservableList<Hangar> hangares = FXCollections.observableArrayList();
    private final ObservableList<Modelo> modelos = FXCollections.observableArrayList();

    public NaveController(NaveService naveService, EmpresaService empresaService,
                          HangarService hangarService, ModeloService modeloService) {
        this.naveService = naveService;
        this.empresaService = empresaService;
        this.hangarService = hangarService;
        this.modeloService = modeloService;
    }

    @FXML private TableView<Nave> naveTable;
    @FXML private TableColumn<Nave, Integer> idColumn;
    @FXML private TableColumn<Nave, String> empresaColumn;
    @FXML private TableColumn<Nave, String> hangarColumn;
    @FXML private TableColumn<Nave, String> modeloColumn;
    @FXML private TableColumn<Nave, Integer> capacidadColumn;
    @FXML private TableColumn<Nave, Integer> pesoColumn;
    @FXML private TableColumn<Nave, String> estadoColumn;
    @FXML private ComboBox<Empresa> empresaCombo;
    @FXML private ComboBox<Hangar> hangarCombo;
    @FXML private ComboBox<Modelo> modeloCombo;
    @FXML private TextField estadoField;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        if (naveTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idNave"));
            empresaColumn.setCellValueFactory(cellData -> {
                Empresa empresa = cellData.getValue().getEmpresa();
                return new SimpleStringProperty(empresa != null ? empresa.getNombre() : "");
            });
            hangarColumn.setCellValueFactory(cellData -> {
                Hangar hangar = cellData.getValue().getHangar();
                return new SimpleStringProperty(hangar != null ? hangar.getDescripcion() : "");
            });
            modeloColumn.setCellValueFactory(cellData -> {
                Modelo modelo = cellData.getValue().getModelo();
                return new SimpleStringProperty(modelo != null ? modelo.getNombreModelo() : "");
            });
            capacidadColumn.setCellValueFactory(cellData -> {
                Modelo modelo = cellData.getValue().getModelo();
                return new javafx.beans.property.SimpleObjectProperty<>(modelo != null ? modelo.getCapacidad() : null);
            });
            pesoColumn.setCellValueFactory(cellData -> {
                Modelo modelo = cellData.getValue().getModelo();
                return new javafx.beans.property.SimpleObjectProperty<>(modelo != null ? modelo.getPeso() : null);
            });
            estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
            naves.setAll(naveService.findAll());
            naveTable.setItems(filteredNaves);
            naveTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
        populateEmpresas();
        populateHangares();
        populateModelos();
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Seleccione empresa, hangar y modelo.");
            return;
        }
        Nave selected = naveTable.getSelectionModel().getSelectedItem();
        Nave nave = selected != null ? naveService.findById(selected.getIdNave()) : new Nave();
        nave.setEmpresa(empresaCombo.getValue());
        nave.setHangar(hangarCombo.getValue());
        nave.setModelo(modeloCombo.getValue());
        nave.setEstado(estadoField.getText().trim());
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
        try {
            naveService.delete(selected.getIdNave());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "La nave seleccionada fue eliminada.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar esta nave porque tiene vuelos o reportes asociados.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar la nave: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (naveTable != null) naveTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) return;
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredNaves.setPredicate(n -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredNaves.setPredicate(nave -> {
            if (nave == null) return false;
            boolean matchesEmpresa = nave.getEmpresa() != null && nave.getEmpresa().getNombre() != null && nave.getEmpresa().getNombre().toLowerCase().contains(normalized);
            boolean matchesModelo = nave.getModelo() != null && nave.getModelo().getNombreModelo() != null && nave.getModelo().getNombreModelo().toLowerCase().contains(normalized);
            boolean matchesEstado = nave.getEstado() != null && nave.getEstado().toLowerCase().contains(normalized);
            return matchesEmpresa || matchesModelo || matchesEstado;
        });
    }

    private void populateEmpresas() {
        empresas.setAll(empresaService.findAll());
        empresas.sort(Comparator.comparing(e -> e.getNombre() != null ? e.getNombre() : "", String.CASE_INSENSITIVE_ORDER));
        if (empresaCombo == null) return;
        empresaCombo.setItems(empresas);
        empresaCombo.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Empresa item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        empresaCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Empresa item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
    }

    private void populateHangares() {
        hangares.setAll(hangarService.findAll());
        hangares.sort(Comparator.comparing(h -> h.getDescripcion() != null ? h.getDescripcion() : "", String.CASE_INSENSITIVE_ORDER));
        if (hangarCombo == null) return;
        hangarCombo.setItems(hangares);
        hangarCombo.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Hangar item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDescripcion());
            }
        });
        hangarCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Hangar item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDescripcion());
            }
        });
    }

    private void populateModelos() {
        modelos.setAll(modeloService.findAll());
        if (modeloCombo == null) return;
        modeloCombo.setItems(modelos);
        modeloCombo.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Modelo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombreModelo());
            }
        });
        modeloCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Modelo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombreModelo());
            }
        });
    }

    private boolean isFormValid() {
        return empresaCombo != null && empresaCombo.getValue() != null
            && hangarCombo != null && hangarCombo.getValue() != null
            && modeloCombo != null && modeloCombo.getValue() != null;
    }

    private void refreshTable() {
        naves.setAll(naveService.findAll());
        naveTable.refresh();
        onBuscar();
    }

    private void fillForm(Nave nave) {
        if (nave == null) { clearForm(); return; }
        if (nave.getEmpresa() != null) empresaCombo.setValue(empresas.stream().filter(e -> e.getIdEmpresa().equals(nave.getEmpresa().getIdEmpresa())).findFirst().orElse(null));
        if (nave.getHangar() != null) hangarCombo.setValue(hangares.stream().filter(h -> h.getIdHangar().equals(nave.getHangar().getIdHangar())).findFirst().orElse(null));
        if (nave.getModelo() != null) modeloCombo.setValue(modelos.stream().filter(m -> m.getIdModelo().equals(nave.getModelo().getIdModelo())).findFirst().orElse(null));
        estadoField.setText(nave.getEstado());
    }

    private void clearForm() {
        if (empresaCombo != null) empresaCombo.setValue(null);
        if (hangarCombo != null) hangarCombo.setValue(null);
        if (modeloCombo != null) modeloCombo.setValue(null);
        if (estadoField != null) estadoField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

