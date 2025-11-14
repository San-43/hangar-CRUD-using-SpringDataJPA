package com.example.hangar.ui.controller;

import com.example.hangar.model.Nave;
import com.example.hangar.model.Tripulacion;
import com.example.hangar.model.Vuelo;
import com.example.hangar.service.NaveService;
import com.example.hangar.service.TripulacionService;
import com.example.hangar.service.VueloService;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class VueloController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final VueloService vueloService;
    private final ObservableList<Vuelo> vuelos = FXCollections.observableArrayList();
    private final ObservableList<Nave> naves = FXCollections.observableArrayList();
    private final ObservableList<Tripulacion> tripulaciones = FXCollections.observableArrayList();
    private final FilteredList<Vuelo> filteredVuelos = new FilteredList<>(vuelos, vuelo -> true);

    private final NaveService naveService;
    private final TripulacionService tripulacionService;

    public VueloController(VueloService vueloService, NaveService naveService, TripulacionService tripulacionService) {
        this.vueloService = vueloService;
        this.naveService = naveService;
        this.tripulacionService = tripulacionService;
    }

    @FXML
    private TableView<Vuelo> vueloTable;

    @FXML
    private TableColumn<Vuelo, String> codigoColumn;

    @FXML
    private TableColumn<Vuelo, String> destinoColumn;

    @FXML
    private TableColumn<Vuelo, String> fechaColumn;

    @FXML
    private TableColumn<Vuelo, String> naveColumn;

    @FXML
    private TableColumn<Vuelo, String> tripulacionColumn;

    @FXML
    private TextField codigoField;

    @FXML
    private TextField destinoField;

    @FXML
    private TextField fechaSalidaField;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Nave> naveCombo;

    @FXML
    private ComboBox<Tripulacion> tripulacionCombo;

    @FXML
    public void initialize() {
        if (vueloTable != null) {
            codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            destinoColumn.setCellValueFactory(new PropertyValueFactory<>("destino"));
            fechaColumn.setCellValueFactory(vuelo -> new SimpleStringProperty(formatFecha(vuelo.getValue().getFechaSalida())));
            naveColumn.setCellValueFactory(vuelo -> new SimpleStringProperty(
                    vuelo.getValue().getNave() != null ? vuelo.getValue().getNave().getMatricula() : ""));
            tripulacionColumn.setCellValueFactory(vuelo -> new SimpleStringProperty(
                    vuelo.getValue().getTripulacion() != null ? vuelo.getValue().getTripulacion().getNombre() : ""));
            vuelos.setAll(vueloService.findAll());
            vueloTable.setItems(filteredVuelos);
            vueloTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }

        if (naveCombo != null) {
            configureNaveCombo();
        }

        if (tripulacionCombo != null) {
            configureTripulacionCombo();
        }

        loadLookupData();
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Código, destino y fecha son obligatorios.");
            return;
        }
        LocalDateTime fecha = parseFecha();
        if (fecha == null) {
            return;
        }
        Vuelo selected = vueloTable.getSelectionModel().getSelectedItem();
        Long excludeId = selected != null ? selected.getId() : null;
        String codigo = codigoField.getText().trim();

        // Validación previa de unicidad de código
        if (!vueloService.isCodigoDisponible(codigo, excludeId)) {
            showAlert(Alert.AlertType.WARNING, "Código duplicado",
                    "Ya existe un vuelo con el código '" + codigo + "'. Ingrese un código diferente.");
            return;
        }

        Vuelo vuelo = selected != null ? vueloService.findById(selected.getId()) : new Vuelo();
        vuelo.setCodigo(codigo);
        vuelo.setDestino(destinoField.getText().trim());
        vuelo.setFechaSalida(fecha);
        vuelo.setNave(naveCombo != null ? naveCombo.getValue() : null);
        vuelo.setTripulacion(tripulacionCombo != null ? tripulacionCombo.getValue() : null);
        try {
            vueloService.save(vuelo);
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "El vuelo ha sido guardado correctamente.");
        } catch (DataIntegrityViolationException ex) {
            // Red de seguridad por si hay condición de carrera o inconsistencia
            showAlert(Alert.AlertType.ERROR, "Código duplicado",
                    "No se pudo guardar porque el código '" + codigo + "' ya existe.");
        }
    }

    @FXML
    private void onEliminar() {
        Vuelo selected = vueloTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un vuelo para eliminarlo.");
            return;
        }
        vueloService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El vuelo seleccionado fue eliminado.");
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (vueloTable != null) {
            vueloTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredVuelos.setPredicate(vuelo -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredVuelos.setPredicate(vuelo -> {
            if (vuelo == null) {
                return false;
            }
            boolean matchCodigo = vuelo.getCodigo() != null && vuelo.getCodigo().toLowerCase().contains(normalized);
            boolean matchDestino = vuelo.getDestino() != null && vuelo.getDestino().toLowerCase().contains(normalized);
            boolean matchFecha = vuelo.getFechaSalida() != null && formatFecha(vuelo.getFechaSalida()).toLowerCase().contains(normalized);
            return matchCodigo || matchDestino || matchFecha;
        });
    }

    private boolean isFormValid() {
        return codigoField != null && !codigoField.getText().isBlank()
                && destinoField != null && !destinoField.getText().isBlank()
                && fechaSalidaField != null && !fechaSalidaField.getText().isBlank();
    }

    private LocalDateTime parseFecha() {
        try {
            return LocalDateTime.parse(fechaSalidaField.getText().trim(), FORMATTER);
        } catch (DateTimeParseException ex) {
            showAlert(Alert.AlertType.ERROR, "Formato inválido", "Use el formato AAAA-MM-DD HH:MM para la fecha de salida.");
            return null;
        }
    }

    private void refreshTable() {
        vuelos.setAll(vueloService.findAll());
        vueloTable.refresh();
        onBuscar();
    }

    private void fillForm(Vuelo vuelo) {
        if (vuelo == null) {
            clearForm();
            return;
        }
        codigoField.setText(vuelo.getCodigo());
        destinoField.setText(vuelo.getDestino());
        fechaSalidaField.setText(vuelo.getFechaSalida() != null ? formatFecha(vuelo.getFechaSalida()) : "");
        if (naveCombo != null) {
            selectNave(vuelo.getNave());
        }
        if (tripulacionCombo != null) {
            selectTripulacion(vuelo.getTripulacion());
        }
    }

    private void clearForm() {
        if (codigoField != null) {
            codigoField.clear();
        }
        if (destinoField != null) {
            destinoField.clear();
        }
        if (fechaSalidaField != null) {
            fechaSalidaField.clear();
        }
        if (naveCombo != null) {
            naveCombo.getSelectionModel().clearSelection();
        }
        if (tripulacionCombo != null) {
            tripulacionCombo.getSelectionModel().clearSelection();
        }
    }

    private String formatFecha(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(FORMATTER);
    }

    private void loadLookupData() {
        naves.setAll(naveService.findAll());
        tripulaciones.setAll(tripulacionService.findAll());
    }

    private void configureNaveCombo() {
        naveCombo.setItems(naves);
        naveCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Nave item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getMatricula());
            }
        });
        naveCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Nave item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getMatricula());
            }
        });
    }

    private void configureTripulacionCombo() {
        tripulacionCombo.setItems(tripulaciones);
        tripulacionCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Tripulacion item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        tripulacionCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tripulacion item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
    }

    private void selectNave(Nave nave) {
        if (nave == null) {
            naveCombo.getSelectionModel().clearSelection();
            return;
        }
        naves.stream()
                .filter(item -> Objects.equals(item.getId(), nave.getId()))
                .findFirst()
                .ifPresentOrElse(
                        item -> naveCombo.getSelectionModel().select(item),
                        () -> naveCombo.getSelectionModel().clearSelection());
    }

    private void selectTripulacion(Tripulacion tripulacion) {
        if (tripulacion == null) {
            tripulacionCombo.getSelectionModel().clearSelection();
            return;
        }
        tripulaciones.stream()
                .filter(item -> Objects.equals(item.getId(), tripulacion.getId()))
                .findFirst()
                .ifPresentOrElse(
                        item -> tripulacionCombo.getSelectionModel().select(item),
                        () -> tripulacionCombo.getSelectionModel().clearSelection());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
