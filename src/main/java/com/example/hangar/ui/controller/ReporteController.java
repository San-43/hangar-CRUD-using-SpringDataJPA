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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Component
public class ReporteController {

    private final ReporteService reporteService;
    private final NaveService naveService;
    private final TallerService tallerService;
    private final EncargadoService encargadoService;
    private final ObservableList<Reporte> reportes = FXCollections.observableArrayList();
    private final FilteredList<Reporte> filteredReportes = new FilteredList<>(reportes, r -> true);
    private final ObservableList<Nave> naves = FXCollections.observableArrayList();
    private final ObservableList<Taller> talleres = FXCollections.observableArrayList();
    private final ObservableList<Encargado> encargados = FXCollections.observableArrayList();

    public ReporteController(ReporteService reporteService, NaveService naveService,
                           TallerService tallerService, EncargadoService encargadoService) {
        this.reporteService = reporteService;
        this.naveService = naveService;
        this.tallerService = tallerService;
        this.encargadoService = encargadoService;
    }

    @FXML private TableView<Reporte> reporteTable;
    @FXML private TableColumn<Reporte, Integer> idColumn;
    @FXML private TableColumn<Reporte, String> naveColumn;
    @FXML private TableColumn<Reporte, String> tallerColumn;
    @FXML private TableColumn<Reporte, String> encargadoColumn;
    @FXML private TableColumn<Reporte, String> fechaColumn;
    @FXML private TableColumn<Reporte, String> costoColumn;
    @FXML private ComboBox<Nave> naveCombo;
    @FXML private ComboBox<Taller> tallerCombo;
    @FXML private ComboBox<Encargado> encargadoCombo;
    @FXML private TextArea diagnosticoArea;
    @FXML private TextArea accionesRealizadasArea;
    @FXML private DatePicker fechaPicker;
    @FXML private TextField costoField;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        if (reporteTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idReporte"));
            naveColumn.setCellValueFactory(cellData -> {
                Nave nave = cellData.getValue().getNave();
                return new SimpleStringProperty(nave != null ? "ID: " + nave.getIdNave() : "");
            });
            tallerColumn.setCellValueFactory(cellData -> {
                Taller taller = cellData.getValue().getTaller();
                return new SimpleStringProperty(taller != null ? "ID: " + taller.getIdTaller() : "");
            });
            encargadoColumn.setCellValueFactory(cellData -> {
                Encargado encargado = cellData.getValue().getEncargado();
                return new SimpleStringProperty(encargado != null ? encargado.getNombre() : "");
            });
            fechaColumn.setCellValueFactory(cellData -> {
                LocalDateTime fecha = cellData.getValue().getFecha();
                String formatted = fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
                return new SimpleStringProperty(formatted);
            });
            costoColumn.setCellValueFactory(cellData -> {
                BigDecimal costo = cellData.getValue().getCosto();
                return new SimpleStringProperty(costo != null ? "$" + costo.toString() : "");
            });
            reportes.setAll(reporteService.findAll());
            reporteTable.setItems(filteredReportes);
            reporteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
        populateNaves();
        populateTalleres();
        populateEncargados();
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Seleccione nave, taller y encargado.");
            return;
        }
        Reporte selected = reporteTable.getSelectionModel().getSelectedItem();
        Reporte reporte = selected != null ? reporteService.findById(selected.getIdReporte()) : new Reporte();

        reporte.setNave(naveCombo.getValue());
        reporte.setTaller(tallerCombo.getValue());
        reporte.setEncargado(encargadoCombo.getValue());
        reporte.setDiagnostico(diagnosticoArea.getText().trim());
        reporte.setAccionesRealizadas(accionesRealizadasArea.getText().trim());

        if (fechaPicker.getValue() != null) {
            reporte.setFecha(fechaPicker.getValue().atStartOfDay());
        }

        String costoText = costoField.getText().trim();
        if (!costoText.isEmpty()) {
            try {
                reporte.setCosto(new BigDecimal(costoText));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Formato incorrecto", "El costo debe ser un número decimal válido.");
                return;
            }
        } else {
            reporte.setCosto(null);
        }

        reporteService.save(reporte);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "El reporte ha sido guardado correctamente.");
    }

    @FXML
    private void onEliminar() {
        Reporte selected = reporteTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un reporte para eliminarlo.");
            return;
        }

        try {
            reporteService.delete(selected.getIdReporte());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El reporte seleccionado fue eliminado.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar el reporte: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (reporteTable != null) {
            reporteTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) return;
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredReportes.setPredicate(r -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredReportes.setPredicate(reporte -> {
            if (reporte == null) return false;
            boolean matchesDiagnostico = reporte.getDiagnostico() != null
                    && reporte.getDiagnostico().toLowerCase().contains(normalized);
            boolean matchesAcciones = reporte.getAccionesRealizadas() != null
                    && reporte.getAccionesRealizadas().toLowerCase().contains(normalized);
            boolean matchesEncargado = reporte.getEncargado() != null && reporte.getEncargado().getNombre() != null
                    && reporte.getEncargado().getNombre().toLowerCase().contains(normalized);
            return matchesDiagnostico || matchesAcciones || matchesEncargado;
        });
    }

    private void populateNaves() {
        naves.setAll(naveService.findAll());
        naves.sort(Comparator.comparing(n -> n.getIdNave()));
        if (naveCombo == null) return;
        naveCombo.setItems(naves);
        naveCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Nave item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "ID: " + item.getIdNave() + " - " + item.getEstado());
            }
        });
        naveCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Nave item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "ID: " + item.getIdNave());
            }
        });
    }

    private void populateTalleres() {
        talleres.setAll(tallerService.findAll());
        talleres.sort(Comparator.comparing(t -> t.getIdTaller()));
        if (tallerCombo == null) return;
        tallerCombo.setItems(talleres);
        tallerCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Taller item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "ID: " + item.getIdTaller());
            }
        });
        tallerCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Taller item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "ID: " + item.getIdTaller());
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
        return naveCombo != null && naveCombo.getValue() != null
                && tallerCombo != null && tallerCombo.getValue() != null
                && encargadoCombo != null && encargadoCombo.getValue() != null;
    }

    private void refreshTable() {
        reportes.setAll(reporteService.findAll());
        reporteTable.refresh();
        onBuscar();
    }

    private void fillForm(Reporte reporte) {
        if (reporte == null) {
            clearForm();
            return;
        }
        if (reporte.getNave() != null) {
            naveCombo.setValue(naves.stream()
                    .filter(n -> n.getIdNave().equals(reporte.getNave().getIdNave()))
                    .findFirst().orElse(null));
        }
        if (reporte.getTaller() != null) {
            tallerCombo.setValue(talleres.stream()
                    .filter(t -> t.getIdTaller().equals(reporte.getTaller().getIdTaller()))
                    .findFirst().orElse(null));
        }
        if (reporte.getEncargado() != null) {
            encargadoCombo.setValue(encargados.stream()
                    .filter(e -> e.getIdEncargado().equals(reporte.getEncargado().getIdEncargado()))
                    .findFirst().orElse(null));
        }
        diagnosticoArea.setText(reporte.getDiagnostico());
        accionesRealizadasArea.setText(reporte.getAccionesRealizadas());
        if (reporte.getFecha() != null) {
            fechaPicker.setValue(reporte.getFecha().toLocalDate());
        }
        costoField.setText(reporte.getCosto() != null ? reporte.getCosto().toString() : "");
    }

    private void clearForm() {
        if (naveCombo != null) naveCombo.setValue(null);
        if (tallerCombo != null) tallerCombo.setValue(null);
        if (encargadoCombo != null) encargadoCombo.setValue(null);
        if (diagnosticoArea != null) diagnosticoArea.clear();
        if (accionesRealizadasArea != null) accionesRealizadasArea.clear();
        if (fechaPicker != null) fechaPicker.setValue(null);
        if (costoField != null) costoField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

