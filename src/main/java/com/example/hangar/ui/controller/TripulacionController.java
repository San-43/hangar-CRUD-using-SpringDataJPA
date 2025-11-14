package com.example.hangar.ui.controller;

import com.example.hangar.model.Persona;
import com.example.hangar.model.Tripulacion;
import com.example.hangar.service.PersonaService;
import com.example.hangar.service.TripulacionService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class TripulacionController {

    private final TripulacionService tripulacionService;
    private final PersonaService personaService;
    private final ObservableList<Tripulacion> tripulaciones = FXCollections.observableArrayList();
    private final FilteredList<Tripulacion> filteredTripulaciones = new FilteredList<>(tripulaciones, tripulacion -> true);
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();

    public TripulacionController(TripulacionService tripulacionService, PersonaService personaService) {
        this.tripulacionService = tripulacionService;
        this.personaService = personaService;
    }

    @FXML
    private TableView<Tripulacion> tripulacionTable;

    @FXML
    private TableColumn<Tripulacion, Long> idColumn;

    @FXML
    private TableColumn<Tripulacion, String> nombreColumn;

    @FXML
    private TableColumn<Tripulacion, Number> integrantesColumn;

    @FXML
    private TableColumn<Tripulacion, Number> vuelosColumn;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Persona> integrantesList;

    @FXML
    public void initialize() {
        refreshPersonas();
        if (tripulacionTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            integrantesColumn.setCellValueFactory(data -> new SimpleIntegerProperty(
                    data.getValue().getIntegrantes() != null ? data.getValue().getIntegrantes().size() : 0));
            vuelosColumn.setCellValueFactory(data -> new SimpleIntegerProperty(
                    data.getValue().getVuelos() != null ? data.getValue().getVuelos().size() : 0));
            tripulaciones.setAll(tripulacionService.findAll());
            tripulacionTable.setItems(filteredTripulaciones);
            tripulacionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }

        if (integrantesList != null) {
            integrantesList.setItems(personas);
            integrantesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            integrantesList.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(Persona persona, boolean empty) {
                    super.updateItem(persona, empty);
                    if (empty || persona == null) {
                        setText(null);
                    } else {
                        String nombreCompleto = String.format("%s %s",
                                persona.getNombres() != null ? persona.getNombres() : "",
                                persona.getApellidos() != null ? persona.getApellidos() : "").trim();
                        setText(nombreCompleto.isBlank() ? persona.getDocumento() : nombreCompleto);
                    }
                }
            });
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Debe ingresar un nombre y seleccionar al menos un integrante.");
            return;
        }
        Tripulacion selected = tripulacionTable.getSelectionModel().getSelectedItem();
        Tripulacion tripulacion = selected != null ? tripulacionService.findById(selected.getId()) : new Tripulacion();
        tripulacion.setNombre(nombreField.getText().trim());
        if (integrantesList != null) {
            Set<Persona> integrantesSeleccionados = new LinkedHashSet<>(integrantesList.getSelectionModel().getSelectedItems());
            tripulacion.setIntegrantes(integrantesSeleccionados);
        }
        tripulacionService.save(tripulacion);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "La tripulación ha sido guardada correctamente.");
    }

    @FXML
    private void onEliminar() {
        Tripulacion selected = tripulacionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir una tripulación para eliminarla.");
            return;
        }

        // Validar si la tripulación tiene registros asociados
        try {
            String constraintMessage = tripulacionService.checkDeletionConstraints(selected.getId());

            if (constraintMessage != null) {
                showAlert(Alert.AlertType.WARNING, "No se puede eliminar", constraintMessage);
                return;
            }

            tripulacionService.delete(selected.getId());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "La tripulación seleccionada fue eliminada.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar esta tripulación porque tiene registros asociados. " +
                    "Primero debe eliminar o reasignar los registros relacionados.");
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
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredTripulaciones.setPredicate(tripulacion -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredTripulaciones.setPredicate(tripulacion -> {
            if (tripulacion == null) {
                return false;
            }
            boolean matchesNombre = tripulacion.getNombre() != null
                    && tripulacion.getNombre().toLowerCase().contains(normalized);
            boolean matchesIntegrante = tripulacion.getIntegrantes() != null
                    && tripulacion.getIntegrantes().stream().anyMatch(persona -> {
                boolean nombreMatch = persona.getNombres() != null
                        && persona.getNombres().toLowerCase().contains(normalized);
                boolean apellidoMatch = persona.getApellidos() != null
                        && persona.getApellidos().toLowerCase().contains(normalized);
                boolean documentoMatch = persona.getDocumento() != null
                        && persona.getDocumento().toLowerCase().contains(normalized);
                return nombreMatch || apellidoMatch || documentoMatch;
            });
            return matchesNombre || matchesIntegrante;
        });
    }

    private boolean isFormValid() {
        boolean nombreValido = nombreField != null && !nombreField.getText().isBlank();
        boolean integrantesValidos = integrantesList != null
                && !integrantesList.getSelectionModel().getSelectedItems().isEmpty();
        return nombreValido && integrantesValidos;
    }

    private void refreshTable() {
        tripulaciones.setAll(tripulacionService.findAll());
        tripulacionTable.refresh();
        onBuscar();
        refreshPersonas();
    }

    private void fillForm(Tripulacion tripulacion) {
        if (tripulacion == null) {
            clearForm();
            return;
        }
        nombreField.setText(tripulacion.getNombre());
        if (integrantesList != null) {
            integrantesList.getSelectionModel().clearSelection();
            if (tripulacion.getIntegrantes() != null) {
                for (Persona integrante : tripulacion.getIntegrantes()) {
                    personas.stream()
                            .filter(p -> p.getId() != null && p.getId().equals(integrante.getId()))
                            .findFirst()
                            .ifPresent(p -> integrantesList.getSelectionModel().select(p));
                }
            }
        }
    }

    private void clearForm() {
        if (nombreField != null) {
            nombreField.clear();
        }
        if (integrantesList != null) {
            integrantesList.getSelectionModel().clearSelection();
        }
    }

    private void refreshPersonas() {
        personas.setAll(personaService.findAll());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
