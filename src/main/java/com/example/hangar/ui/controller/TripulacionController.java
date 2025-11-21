package com.example.hangar.ui.controller;

import com.example.hangar.model.Persona;
import com.example.hangar.model.Tripulacion;
import com.example.hangar.model.Vuelo;
import com.example.hangar.service.PersonaService;
import com.example.hangar.service.TripulacionService;
import com.example.hangar.service.VueloService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Component
public class TripulacionController {

    private final TripulacionService tripulacionService;
    private final VueloService vueloService;
    private final PersonaService personaService;
    private final ObservableList<Tripulacion> tripulaciones = FXCollections.observableArrayList();
    private final FilteredList<Tripulacion> filteredTripulaciones = new FilteredList<>(tripulaciones, t -> true);
    private final ObservableList<Vuelo> vuelos = FXCollections.observableArrayList();
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();

    // Catálogo de roles de tripulación
    private final List<String> rolesDisponibles = Arrays.asList(
        "Capitán",
        "Copiloto",
        "Ingeniero de Vuelo",
        "Auxiliar de Vuelo"
    );

    public TripulacionController(TripulacionService tripulacionService, VueloService vueloService,
                                 PersonaService personaService) {
        this.tripulacionService = tripulacionService;
        this.vueloService = vueloService;
        this.personaService = personaService;
    }

    @FXML private TableView<Tripulacion> tripulacionTable;
    @FXML private TableColumn<Tripulacion, Integer> idColumn;
    @FXML private TableColumn<Tripulacion, String> vueloColumn;
    @FXML private TableColumn<Tripulacion, String> personaColumn;
    @FXML private TableColumn<Tripulacion, String> rolColumn;
    @FXML private ComboBox<Vuelo> vueloCombo;
    @FXML private ComboBox<Persona> personaCombo;
    @FXML private ComboBox<String> rolCombo;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        if (tripulacionTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("idTripulacion"));
            vueloColumn.setCellValueFactory(cellData -> {
                Vuelo vuelo = cellData.getValue().getVuelo();
                return new SimpleStringProperty(vuelo != null ?
                    (vuelo.getOrigen() + " → " + vuelo.getDestino()) : "");
            });
            personaColumn.setCellValueFactory(cellData -> {
                Persona persona = cellData.getValue().getPersona();
                return new SimpleStringProperty(persona != null ? persona.getNombre() : "");
            });
            rolColumn.setCellValueFactory(cellData -> {
                String rol = cellData.getValue().getRolTripulacion();
                return new SimpleStringProperty(rol != null ? rol : "");
            });
            tripulaciones.setAll(tripulacionService.findAll());
            tripulacionTable.setItems(filteredTripulaciones);
            tripulacionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }
        populateVuelos();
        populatePersonas();
        populateRoles();
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Seleccione vuelo, persona y rol.");
            return;
        }

        Vuelo vueloSeleccionado = vueloCombo.getValue();
        Persona personaSeleccionada = personaCombo.getValue();
        String rolSeleccionado = rolCombo.getValue();

        // Validar que solo haya un Capitán por vuelo
        if ("Capitán".equals(rolSeleccionado)) {
            Tripulacion selected = tripulacionTable.getSelectionModel().getSelectedItem();
            Integer idTripulacionActual = selected != null ? selected.getIdTripulacion() : null;

            // Contar capitanes existentes en este vuelo (excluyendo el actual si es edición)
            List<Tripulacion> tripulacionesVuelo = tripulacionService.findAll().stream()
                .filter(t -> t.getVuelo() != null && t.getVuelo().getIdVuelo().equals(vueloSeleccionado.getIdVuelo()))
                .toList();
            long capitanesExistentes = tripulacionesVuelo.stream()
                .filter(t -> "Capitán".equals(t.getRolTripulacion()))
                .filter(t -> idTripulacionActual == null || !t.getIdTripulacion().equals(idTripulacionActual))
                .count();

            if (capitanesExistentes > 0) {
                showAlert(Alert.AlertType.ERROR, "Validación fallida",
                    "Este vuelo ya tiene un Capitán. Solo puede haber un Capitán por vuelo.");
                return;
            }
        }

        try {
            Tripulacion selected = tripulacionTable.getSelectionModel().getSelectedItem();
            Tripulacion tripulacion;

            if (selected != null) {
                tripulacion = selected;
                tripulacion.setVuelo(vueloSeleccionado);
                tripulacion.setPersona(personaSeleccionada);
                tripulacion.setRolTripulacion(rolSeleccionado);
                tripulacionService.save(tripulacion);
                showAlert(Alert.AlertType.INFORMATION, "Registro actualizado", "La tripulación fue actualizada correctamente.");
            } else {
                tripulacion = new Tripulacion();
                tripulacion.setVuelo(vueloSeleccionado);
                tripulacion.setPersona(personaSeleccionada);
                tripulacion.setRolTripulacion(rolSeleccionado);
                tripulacionService.save(tripulacion);
                showAlert(Alert.AlertType.INFORMATION, "Registro guardado", "La tripulación fue guardada correctamente.");
            }

            refreshTable();
            clearForm();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al guardar la tripulación: " + e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        Tripulacion selected = tripulacionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir una tripulación para eliminarla.");
            return;
        }

        try {
            String constraintMessage = tripulacionService.checkDeletionConstraints(selected.getIdTripulacion());
            if (constraintMessage != null) {
                showAlert(Alert.AlertType.WARNING, "No se puede eliminar", constraintMessage);
                return;
            }

            tripulacionService.delete(selected.getIdTripulacion());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "La tripulación seleccionada fue eliminada.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar la tripulación: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (tripulacionTable != null) {
            tripulacionTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) return;
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredTripulaciones.setPredicate(t -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredTripulaciones.setPredicate(tripulacion -> {
            if (tripulacion == null) return false;
            boolean matchesPersona = tripulacion.getPersona() != null && tripulacion.getPersona().getNombre() != null
                    && tripulacion.getPersona().getNombre().toLowerCase().contains(normalized);
            boolean matchesRol = tripulacion.getRolTripulacion() != null
                    && tripulacion.getRolTripulacion().toLowerCase().contains(normalized);
            return matchesPersona || matchesRol;
        });
    }

    private void populateVuelos() {
        vuelos.setAll(vueloService.findAll());
        vuelos.sort(Comparator.comparing(v -> v.getIdVuelo()));
        if (vueloCombo == null) return;
        vueloCombo.setItems(vuelos);
        vueloCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Vuelo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                    "ID: " + item.getIdVuelo() + " - " + item.getOrigen() + " → " + item.getDestino());
            }
        });
        vueloCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Vuelo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getOrigen() + " → " + item.getDestino());
            }
        });
    }

    private void populatePersonas() {
        personas.setAll(personaService.findAll());
        personas.sort(Comparator.comparing(p -> p.getNombre() != null ? p.getNombre() : "", String.CASE_INSENSITIVE_ORDER));
        if (personaCombo == null) return;
        personaCombo.setItems(personas);
        personaCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Persona item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        personaCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Persona item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
    }

    private void populateRoles() {
        if (rolCombo == null) return;
        rolCombo.setItems(FXCollections.observableArrayList(rolesDisponibles));
    }

    private boolean isFormValid() {
        return vueloCombo != null && vueloCombo.getValue() != null
                && personaCombo != null && personaCombo.getValue() != null
                && rolCombo != null && rolCombo.getValue() != null;
    }

    private void refreshTable() {
        tripulaciones.setAll(tripulacionService.findAll());
        tripulacionTable.refresh();
        onBuscar();
    }

    private void fillForm(Tripulacion tripulacion) {
        if (tripulacion == null) {
            clearForm();
            return;
        }
        if (tripulacion.getVuelo() != null) {
            vueloCombo.setValue(vuelos.stream()
                    .filter(v -> v.getIdVuelo().equals(tripulacion.getVuelo().getIdVuelo()))
                    .findFirst().orElse(null));
        }
        if (tripulacion.getPersona() != null) {
            personaCombo.setValue(personas.stream()
                    .filter(p -> p.getIdPersona().equals(tripulacion.getPersona().getIdPersona()))
                    .findFirst().orElse(null));
        }
        if (tripulacion.getRolTripulacion() != null) {
            rolCombo.setValue(tripulacion.getRolTripulacion());
        }
    }

    private void clearForm() {
        if (vueloCombo != null) vueloCombo.setValue(null);
        if (personaCombo != null) personaCombo.setValue(null);
        if (rolCombo != null) rolCombo.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

