package com.example.hangar.ui.controller;

import com.example.hangar.model.Nave;
import com.example.hangar.model.Vuelo;
import com.example.hangar.service.NaveService;
import com.example.hangar.service.TripulacionService;
import com.example.hangar.service.VueloService;
import com.example.hangar.util.TripulacionValidator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Component
public class VueloController {

    private final VueloService vueloService;
    private final NaveService naveService;
    private final TripulacionService tripulacionService;
    private final ObservableList<Vuelo> vuelos = FXCollections.observableArrayList();
    private final FilteredList<Vuelo> filteredVuelos = new FilteredList<>(vuelos, vuelo -> true);
    private final ObservableList<Nave> naves = FXCollections.observableArrayList();

    public VueloController(VueloService vueloService, NaveService naveService, TripulacionService tripulacionService) {
        this.vueloService = vueloService;
        this.naveService = naveService;
        this.tripulacionService = tripulacionService;
    }

    @FXML private TableView<Vuelo> vueloTable;
    @FXML private TableColumn<Vuelo, Integer> idColumn;
    @FXML private TableColumn<Vuelo, String> naveColumn;
    @FXML private TableColumn<Vuelo, String> origenColumn;
    @FXML private TableColumn<Vuelo, String> destinoColumn;
    @FXML private TableColumn<Vuelo, String> fechaSalidaColumn;
    @FXML private TableColumn<Vuelo, String> fechaLlegadaColumn;
    @FXML private TableColumn<Vuelo, Integer> pasajerosColumn;
    @FXML private TableColumn<Vuelo, Integer> distanciaColumn;
    @FXML private ComboBox<Nave> naveCombo;
    @FXML private TextField origenField;
    @FXML private TextField destinoField;
    @FXML private DatePicker fechaSalidaPicker;
    @FXML private DatePicker fechaLlegadaPicker;
    @FXML private TextField pasajerosField;
    @FXML private TextField distanciaField;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        if (vueloTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idVuelo"));
            naveColumn.setCellValueFactory(cellData -> {
                Nave nave = cellData.getValue().getNave();
                if (nave == null) {
                    return new SimpleStringProperty("");
                }
                StringBuilder sb = new StringBuilder();
                if (nave.getEmpresa() != null && nave.getEmpresa().getNombre() != null) {
                    sb.append(nave.getEmpresa().getNombre()).append(" - ");
                }
                if (nave.getModelo() != null && nave.getModelo().getNombreModelo() != null) {
                    sb.append(nave.getModelo().getNombreModelo());
                } else {
                    sb.append("Nave ").append(nave.getIdNave());
                }
                return new SimpleStringProperty(sb.toString());
            });
            origenColumn.setCellValueFactory(new PropertyValueFactory<>("origen"));
            destinoColumn.setCellValueFactory(new PropertyValueFactory<>("destino"));
            fechaSalidaColumn.setCellValueFactory(cellData -> {
                LocalDateTime fecha = cellData.getValue().getFechaSalida();
                String formatted = fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
                return new SimpleStringProperty(formatted);
            });
            fechaLlegadaColumn.setCellValueFactory(cellData -> {
                LocalDateTime fecha = cellData.getValue().getFechaLlegada();
                String formatted = fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
                return new SimpleStringProperty(formatted);
            });
            pasajerosColumn.setCellValueFactory(new PropertyValueFactory<>("pasajeros"));
            distanciaColumn.setCellValueFactory(new PropertyValueFactory<>("distancia"));
            vuelos.setAll(vueloService.findAll());
            vueloTable.setItems(filteredVuelos);
            vueloTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
        populateNaves();
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Complete nave, origen y destino.");
            return;
        }
        Vuelo selected = vueloTable.getSelectionModel().getSelectedItem();
        Vuelo vuelo = selected != null ? vueloService.findById(selected.getIdVuelo()) : new Vuelo();

        vuelo.setNave(naveCombo.getValue());
        vuelo.setOrigen(origenField.getText().trim());
        vuelo.setDestino(destinoField.getText().trim());

        if (fechaSalidaPicker.getValue() != null) {
            vuelo.setFechaSalida(fechaSalidaPicker.getValue().atStartOfDay());
        }
        if (fechaLlegadaPicker.getValue() != null) {
            vuelo.setFechaLlegada(fechaLlegadaPicker.getValue().atStartOfDay());
        }

        String pasajerosText = pasajerosField.getText().trim();
        if (!pasajerosText.isEmpty()) {
            try {
                vuelo.setPasajeros(Integer.parseInt(pasajerosText));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Formato incorrecto", "Los pasajeros deben ser un número entero.");
                return;
            }
        } else {
            vuelo.setPasajeros(null);
        }

        String distanciaText = distanciaField.getText().trim();
        if (!distanciaText.isEmpty()) {
            try {
                vuelo.setDistancia(Integer.parseInt(distanciaText));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Formato incorrecto", "La distancia debe ser un número entero.");
                return;
            }
        } else {
            vuelo.setDistancia(null);
        }

        vueloService.save(vuelo);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "El vuelo ha sido guardado correctamente.");
    }

    @FXML
    private void onEliminar() {
        Vuelo selected = vueloTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un vuelo para eliminarlo.");
            return;
        }

        try {
            vueloService.delete(selected.getIdVuelo());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El vuelo seleccionado fue eliminado.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar este vuelo porque tiene tripulaciones asociadas.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar el vuelo: " + e.getMessage());
        }
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
        if (searchField == null) return;
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredVuelos.setPredicate(vuelo -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredVuelos.setPredicate(vuelo -> {
            if (vuelo == null) return false;
            boolean matchesOrigen = vuelo.getOrigen() != null && vuelo.getOrigen().toLowerCase().contains(normalized);
            boolean matchesDestino = vuelo.getDestino() != null && vuelo.getDestino().toLowerCase().contains(normalized);
            return matchesOrigen || matchesDestino;
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
                if (empty || item == null) {
                    setText(null);
                } else {
                    StringBuilder sb = new StringBuilder();
                    if (item.getEmpresa() != null && item.getEmpresa().getNombre() != null) {
                        sb.append(item.getEmpresa().getNombre()).append(" - ");
                    }
                    if (item.getModelo() != null && item.getModelo().getNombreModelo() != null) {
                        sb.append(item.getModelo().getNombreModelo());
                    } else {
                        sb.append("Nave ").append(item.getIdNave());
                    }
                    setText(sb.toString());
                }
            }
        });
        naveCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Nave item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    StringBuilder sb = new StringBuilder();
                    if (item.getEmpresa() != null && item.getEmpresa().getNombre() != null) {
                        sb.append(item.getEmpresa().getNombre()).append(" - ");
                    }
                    if (item.getModelo() != null && item.getModelo().getNombreModelo() != null) {
                        sb.append(item.getModelo().getNombreModelo());
                    } else {
                        sb.append("Nave ").append(item.getIdNave());
                    }
                    setText(sb.toString());
                }
            }
        });
    }

    private boolean isFormValid() {
        return naveCombo != null && naveCombo.getValue() != null
                && origenField != null && !origenField.getText().isBlank()
                && destinoField != null && !destinoField.getText().isBlank();
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
        if (vuelo.getNave() != null) {
            naveCombo.setValue(naves.stream()
                    .filter(n -> n.getIdNave().equals(vuelo.getNave().getIdNave()))
                    .findFirst().orElse(null));
        }
        origenField.setText(vuelo.getOrigen());
        destinoField.setText(vuelo.getDestino());
        if (vuelo.getFechaSalida() != null) {
            fechaSalidaPicker.setValue(vuelo.getFechaSalida().toLocalDate());
        }
        if (vuelo.getFechaLlegada() != null) {
            fechaLlegadaPicker.setValue(vuelo.getFechaLlegada().toLocalDate());
        }
        pasajerosField.setText(vuelo.getPasajeros() != null ? vuelo.getPasajeros().toString() : "");
        distanciaField.setText(vuelo.getDistancia() != null ? vuelo.getDistancia().toString() : "");
    }

    private void clearForm() {
        if (naveCombo != null) naveCombo.setValue(null);
        if (origenField != null) origenField.clear();
        if (destinoField != null) destinoField.clear();
        if (fechaSalidaPicker != null) fechaSalidaPicker.setValue(null);
        if (fechaLlegadaPicker != null) fechaLlegadaPicker.setValue(null);
        if (pasajerosField != null) pasajerosField.clear();
        if (distanciaField != null) distanciaField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onVerificarTripulacion() {
        Vuelo selected = vueloTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un vuelo",
                "Debe seleccionar un vuelo para verificar su tripulación.");
            return;
        }

        try {
            var tripulaciones = tripulacionService.findAll().stream()
                .filter(t -> t.getVuelo() != null && t.getVuelo().getIdVuelo().equals(selected.getIdVuelo()))
                .toList();

            TripulacionValidator.TripulacionValidationResult resultado = TripulacionValidator.validar(tripulaciones);

            if (resultado.isValid) {
                showAlert(Alert.AlertType.INFORMATION, "Tripulación válida", resultado.message);
            } else {
                showAlert(Alert.AlertType.WARNING, "Tripulación incompleta",
                    resultado.message + "\n\nRequerimientos:\n" +
                    "- Capitán: 1 (encontrado: " + resultado.capitanes + ")\n" +
                    "- Copiloto: 1 (encontrado: " + resultado.copilotos + ")\n" +
                    "- Ingeniero de Vuelo: 1 (encontrado: " + resultado.ingenieros + ")\n" +
                    "- Auxiliares de Vuelo: ≥2 (encontrados: " + resultado.auxiliares + ")");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                "Ocurrió un error al verificar la tripulación: " + e.getMessage());
        }
    }
}
