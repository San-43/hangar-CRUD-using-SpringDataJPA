package com.example.hangar.ui.controller;

import com.example.hangar.model.Nave;
import com.example.hangar.model.Reporte;
import com.example.hangar.model.Taller;
import com.example.hangar.service.NaveService;
import com.example.hangar.service.ReporteService;
import com.example.hangar.service.TallerService;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

@Component
public class ReporteController {

    private final ReporteService reporteService;
    private final TallerService tallerService;
    private final NaveService naveService;
    private final ObservableList<Reporte> reportes = FXCollections.observableArrayList();
    private final FilteredList<Reporte> filteredReportes = new FilteredList<>(reportes, reporte -> true);
    private final ObservableList<Taller> talleres = FXCollections.observableArrayList();
    private final ObservableList<Nave> naves = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReporteController(ReporteService reporteService,
                             TallerService tallerService,
                             NaveService naveService) {
        this.reporteService = reporteService;
        this.tallerService = tallerService;
        this.naveService = naveService;
    }

    @FXML
    private TableView<Reporte> reporteTable;

    @FXML
    private TableColumn<Reporte, Long> idColumn;

    @FXML
    private TableColumn<Reporte, String> tituloColumn;

    @FXML
    private TableColumn<Reporte, String> descripcionColumn;

    @FXML
    private TableColumn<Reporte, String> fechaColumn;

    @FXML
    private TableColumn<Reporte, String> tallerColumn;

    @FXML
    private TableColumn<Reporte, String> naveColumn;

    @FXML
    private TextField diagnosticoField;

    @FXML
    private TextArea descripcionArea;

    @FXML
    private ComboBox<Taller> tallerCombo;

    @FXML
    private ComboBox<Nave> naveCombo;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (reporteTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            tituloColumn.setCellValueFactory(new PropertyValueFactory<>("titulo"));
            descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
            fechaColumn.setCellValueFactory(cellData -> {
                LocalDateTime fecha = cellData.getValue().getFechaRegistro();
                return new SimpleStringProperty(fecha != null ? fecha.format(DATE_FORMATTER) : "");
            });
            tallerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getTaller() != null ? cellData.getValue().getTaller().getNombre() : ""));
            naveColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getNave() != null ? cellData.getValue().getNave().getMatricula() : ""));
            reportes.setAll(reporteService.findAll());
            reporteTable.setItems(filteredReportes);
            reporteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }

        populateComboBox(tallerCombo, talleres, tallerService.findAll(), Taller::getNombre);
        populateComboBox(naveCombo, naves, naveService.findAll(), Nave::getMatricula);
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "El título, taller y nave son obligatorios.");
            return;
        }
        Reporte selected = reporteTable.getSelectionModel().getSelectedItem();
        Reporte reporte = selected != null ? reporteService.findById(selected.getId()) : new Reporte();
        reporte. setDiagnostico(diagnosticoField.getText().trim());
        reporte. setAcciones_realizadas(descripcionArea.getText() != null ? descripcionArea.getText().trim() : "");
        reporte.setTaller(tallerCombo.getValue());
        reporte.setNave(naveCombo.getValue());
        if (reporte.getId() == null) {
            reporte.setFechaRegistro(LocalDateTime.now());
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
        reporteService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El reporte seleccionado fue eliminado.");
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (reporteTable != null) {
            reporteTable.getSelectionModel().clearSelection();
        }
        if (tallerCombo != null) {
            tallerCombo.getSelectionModel().clearSelection();
        }
        if (naveCombo != null) {
            naveCombo.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredReportes.setPredicate(reporte -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredReportes.setPredicate(reporte -> {
            if (reporte == null) {
                return false;
            }
            boolean matchesTitulo = reporte.getDiagnostico() != null && reporte.getDiagnostico().toLowerCase().contains(normalized);
            boolean matchesDescripcion = reporte.getAcciones_realizadas() != null && reporte.getAcciones_realizadas().toLowerCase().contains(normalized);
            return matchesTitulo || matchesDescripcion;
        });
    }

    private boolean isFormValid() {
        return diagnosticoField != null && !diagnosticoField.getText().isBlank()
                && tallerCombo != null && tallerCombo.getValue() != null
                && naveCombo != null && naveCombo.getValue() != null;
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
        diagnosticoField.setText(reporte.getDiagnostico());
        descripcionArea.setText(reporte.getAcciones_realizadas());
        if (tallerCombo != null && reporte.getTaller() != null) {
            Taller taller = talleres.stream()
                    .filter(t -> t.getId().equals(reporte.getTaller().getId()))
                    .findFirst()
                    .orElse(null);
            tallerCombo.getSelectionModel().select(taller);
        }
        if (naveCombo != null && reporte.getNave() != null) {
            Nave nave = naves.stream()
                    .filter(n -> n.getId().equals(reporte.getNave().getId()))
                    .findFirst()
                    .orElse(null);
            naveCombo.getSelectionModel().select(nave);
        }
    }

    private void clearForm() {
        if (diagnosticoField != null) {
            diagnosticoField.clear();
        }
        if (descripcionArea != null) {
            descripcionArea.clear();
        }
        if (tallerCombo != null) {
            tallerCombo.getSelectionModel().clearSelection();
        }
        if (naveCombo != null) {
            naveCombo.getSelectionModel().clearSelection();
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

