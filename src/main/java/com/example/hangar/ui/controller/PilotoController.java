package com.example.hangar.ui.controller;

import com.example.hangar.model.Persona;
import com.example.hangar.model.Piloto;
import com.example.hangar.model.PilotoNave;
import com.example.hangar.service.PersonaService;
import com.example.hangar.service.PilotoNaveService;
import com.example.hangar.service.PilotoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class PilotoController {

    private final PilotoService pilotoService;
    private final PersonaService personaService;
    private final PilotoNaveService pilotoNaveService;
    private final ObservableList<Piloto> pilotos = FXCollections.observableArrayList();
    private final FilteredList<Piloto> filteredPilotos = new FilteredList<>(pilotos, piloto -> true);
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();
    private final ObservableList<PilotoNave> navesPilotadas = FXCollections.observableArrayList();

    public PilotoController(PilotoService pilotoService, PersonaService personaService,
                           PilotoNaveService pilotoNaveService) {
        this.pilotoService = pilotoService;
        this.personaService = personaService;
        this.pilotoNaveService = pilotoNaveService;
    }

    @FXML
    private TableView<Piloto> pilotoTable;

    @FXML
    private TableColumn<Piloto, Integer> idColumn;

    @FXML
    private TableColumn<Piloto, String> personaColumn;

    @FXML
    private TableColumn<Piloto, String> licenciaTipoColumn;

    @FXML
    private TableColumn<Piloto, String> certificacionesColumn;

    @FXML
    private ComboBox<Persona> personaCombo;

    @FXML
    private TextField licenciaTipoField;

    @FXML
    private TextArea certificacionesArea;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<PilotoNave> navesTable;

    @FXML
    private TableColumn<PilotoNave, Integer> naveIdColumn;

    @FXML
    private TableColumn<PilotoNave, String> naveModeloColumn;

    @FXML
    private TableColumn<PilotoNave, String> naveMatriculaColumn;

    @FXML
    private TableColumn<PilotoNave, LocalDate> fechaCertifColumn;

    @FXML
    private TableColumn<PilotoNave, LocalDate> fechaExpiracionColumn;

    @FXML
    private TableColumn<PilotoNave, Integer> horasColumn;

    @FXML
    private TableColumn<PilotoNave, String> estadoColumn;

    @FXML
    public void initialize() {
        try {
            // Inicializar datos de personas
            populatePersonas();

            // Configurar tabla de pilotos
            if (pilotoTable != null) {
                idColumn.setCellValueFactory(new PropertyValueFactory<>("idPiloto"));
                personaColumn.setCellValueFactory(cellData -> {
                    Persona persona = cellData.getValue().getPersona();
                    return new SimpleStringProperty(persona != null ? persona.getNombre() : "");
                });
                licenciaTipoColumn.setCellValueFactory(new PropertyValueFactory<>("licenciaTipo"));
                certificacionesColumn.setCellValueFactory(cellData -> {
                    String cert = cellData.getValue().getCertificaciones();
                    return new SimpleStringProperty(cert != null ? cert : "");
                });
                pilotos.setAll(pilotoService.findAll());
                pilotoTable.setItems(filteredPilotos);
                pilotoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                    try {
                        fillForm(newSel);
                        loadNavesPilotadas(newSel);
                    } catch (Exception e) {
                        System.err.println("Error al seleccionar piloto: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }

            // Configurar tabla de naves pilotadas (solo lectura)
            if (navesTable != null) {
                naveIdColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getNave().getIdNave()));
                naveModeloColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getNave().getModelo() != null ?
                        cellData.getValue().getNave().getModelo().getNombreModelo() : ""));
                naveMatriculaColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty("Nave " + cellData.getValue().getNave().getIdNave()));
                fechaCertifColumn.setCellValueFactory(new PropertyValueFactory<>("fechaCertificacion"));
                fechaExpiracionColumn.setCellValueFactory(new PropertyValueFactory<>("fechaExpiracion"));
                horasColumn.setCellValueFactory(new PropertyValueFactory<>("horasEnNave"));
                estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));

                navesTable.setItems(navesPilotadas);
            }
        } catch (Exception e) {
            System.err.println("Error en PilotoController.initialize(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadNavesPilotadas(Piloto piloto) {
        navesPilotadas.clear();
        if (piloto != null && piloto.getIdPiloto() != null) {
            List<PilotoNave> naves = pilotoNaveService.findByPilotoId(piloto.getIdPiloto());
            navesPilotadas.setAll(naves);
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Seleccione una persona.");
            return;
        }
        Piloto selected = pilotoTable.getSelectionModel().getSelectedItem();
        Piloto piloto = selected != null ? pilotoService.findById(selected.getIdPiloto()) : new Piloto();

        piloto.setPersona(personaCombo.getValue());
        piloto.setLicenciaTipo(licenciaTipoField.getText().trim());
        piloto.setCertificaciones(certificacionesArea.getText().trim());

        pilotoService.save(piloto);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "El piloto ha sido guardado correctamente.");
    }

    @FXML
    private void onEliminar() {
        Piloto selected = pilotoTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un piloto para eliminarlo.");
            return;
        }

        try {
            String constraintMessage = pilotoService.checkDeletionConstraints(selected.getIdPiloto());
            if (constraintMessage != null) {
                showAlert(Alert.AlertType.WARNING, "No se puede eliminar", constraintMessage);
                return;
            }

            pilotoService.delete(selected.getIdPiloto());
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El piloto seleccionado fue eliminado.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "No se puede eliminar",
                    "No se puede eliminar este piloto porque tiene registros asociados.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar el piloto: " + e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (pilotoTable != null) {
            pilotoTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredPilotos.setPredicate(piloto -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredPilotos.setPredicate(piloto -> {
            if (piloto == null) {
                return false;
            }
            boolean matchesPersona = piloto.getPersona() != null && piloto.getPersona().getNombre() != null
                    && piloto.getPersona().getNombre().toLowerCase().contains(normalized);
            boolean matchesLicencia = piloto.getLicenciaTipo() != null
                    && piloto.getLicenciaTipo().toLowerCase().contains(normalized);
            boolean matchesCert = piloto.getCertificaciones() != null
                    && piloto.getCertificaciones().toLowerCase().contains(normalized);
            return matchesPersona || matchesLicencia || matchesCert;
        });
    }

    private void populatePersonas() {
        try {
            if (personaService == null) {
                System.err.println("personaService is null");
                return;
            }
            List<Persona> allPersonas = personaService.findAll();
            personas.setAll(allPersonas);
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
        } catch (Exception e) {
            System.err.println("Error en populatePersonas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isFormValid() {
        return personaCombo != null && personaCombo.getValue() != null;
    }

    private void refreshTable() {
        pilotos.setAll(pilotoService.findAll());
        pilotoTable.refresh();
        onBuscar();
    }

    private void fillForm(Piloto piloto) {
        if (piloto == null) {
            clearForm();
            return;
        }
        if (piloto.getPersona() != null) {
            personaCombo.setValue(personas.stream()
                    .filter(p -> p.getIdPersona().equals(piloto.getPersona().getIdPersona()))
                    .findFirst()
                    .orElse(null));
        }
        licenciaTipoField.setText(piloto.getLicenciaTipo() != null ? piloto.getLicenciaTipo() : "");
        certificacionesArea.setText(piloto.getCertificaciones() != null ? piloto.getCertificaciones() : "");
    }

    private void clearForm() {
        if (personaCombo != null) personaCombo.setValue(null);
        if (licenciaTipoField != null) licenciaTipoField.clear();
        if (certificacionesArea != null) certificacionesArea.clear();
        navesPilotadas.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

