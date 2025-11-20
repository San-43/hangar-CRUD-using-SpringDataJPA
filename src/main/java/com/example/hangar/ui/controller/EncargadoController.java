package com.example.hangar.ui.controller;

import com.example.hangar.model.Encargado;
import com.example.hangar.model.Hangar;
import com.example.hangar.model.Persona;
import com.example.hangar.service.EncargadoService;
import com.example.hangar.service.HangarService;
import com.example.hangar.service.PersonaService;
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

import java.util.Comparator;
import java.util.Optional;

@Component
public class EncargadoController {

    private final EncargadoService encargadoService;
    private final PersonaService personaService;
    private final HangarService hangarService;

    private final ObservableList<Encargado> encargados = FXCollections.observableArrayList();
    private final FilteredList<Encargado> filteredEncargados = new FilteredList<>(encargados, encargado -> true);
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();
    private final ObservableList<Hangar> hangares = FXCollections.observableArrayList();

    public EncargadoController(EncargadoService encargadoService,
                               PersonaService personaService,
                               HangarService hangarService) {
        this.encargadoService = encargadoService;
        this.personaService = personaService;
        this.hangarService = hangarService;
    }

    @FXML
    private TableView<Encargado> encargadoTable;

    @FXML
    private TableColumn<Encargado, Long> idColumn;

    @FXML
    private TableColumn<Encargado, String> personaColumn;

    @FXML
    private TableColumn<Encargado, String> documentoColumn;

    @FXML
    private TableColumn<Encargado, String> hangarColumn;

    @FXML
    private ComboBox<Persona> personaCombo;

    @FXML
    private ComboBox<Hangar> hangarCombo;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (encargadoTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            personaColumn.setCellValueFactory(cellData -> {
                Persona persona = cellData.getValue().getPersona();
                String nombre = persona != null ? (persona.getNombres() + " " + persona.getApellidos()).trim() : "";
                return new SimpleStringProperty(nombre);
            });
            documentoColumn.setCellValueFactory(cellData -> {
                Persona persona = cellData.getValue().getPersona();
                String documento = persona != null ? persona.getDocumento() : "";
                return new SimpleStringProperty(documento);
            });
            hangarColumn.setCellValueFactory(cellData -> {
                Hangar hangar = cellData.getValue().getHangar();
                return new SimpleStringProperty(hangar != null ? hangar.getCodigo() : "");
            });
            encargadoTable.setItems(filteredEncargados);
            encargadoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }

        populatePersonas();
        populateHangares();
        refreshTable();
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Seleccione persona y hangar antes de guardar.");
            return;
        }
        Encargado selected = encargadoTable != null ? encargadoTable.getSelectionModel().getSelectedItem() : null;
        Encargado encargado = selected != null ? encargadoService.findById(selected.getId()) : new Encargado();
        Persona persona = personaCombo.getValue();
        Hangar hangar = hangarCombo.getValue();

        if (hangar != null) {
            Optional<Encargado> existingByHangar = encargadoService.findByHangarId(hangar.getId());
            if (existingByHangar.filter(found -> isDifferentRecord(encargado, found)).isPresent()) {
                showAlert(Alert.AlertType.ERROR,
                        "Hangar ocupado",
                        "El hangar seleccionado ya tiene un encargado asignado.");
                return;
            }
        }

        if (persona != null) {
            Optional<Encargado> existingByPersona = encargadoService.findByPersonaId(persona.getId());
            if (existingByPersona.filter(found -> isDifferentRecord(encargado, found)).isPresent()) {
                showAlert(Alert.AlertType.ERROR,
                        "Persona asignada",
                        "La persona seleccionada ya es encargada de otro hangar.");
                return;
            }
        }

        encargado.setPersona(persona);
        encargado.setHangar(hangar);
        encargadoService.save(encargado);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Encargado guardado", "Los datos fueron almacenados correctamente.");
    }

    @FXML
    private void onEliminar() {
        if (encargadoTable == null) {
            return;
        }
        Encargado selected = encargadoTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un registro", "Debe elegir un encargado para eliminarlo.");
            return;
        }
        encargadoService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Encargado eliminado", "El encargado seleccionado fue eliminado.");
    }

    @FXML
    private void onLimpiar() {
        clearForm();
        if (encargadoTable != null) {
            encargadoTable.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void onBuscar() {
        if (searchField == null) {
            return;
        }
        String term = searchField.getText();
        if (term == null || term.isBlank()) {
            filteredEncargados.setPredicate(encargado -> true);
            return;
        }
        String normalized = term.trim().toLowerCase();
        filteredEncargados.setPredicate(encargado -> {
            if (encargado == null) {
                return false;
            }
            Persona persona = encargado.getPersona();
            Hangar hangar = encargado.getHangar();
            boolean matchesNombre = persona != null && persona.getNombres() != null
                    && persona.getNombres().toLowerCase().contains(normalized);
            boolean matchesApellido = persona != null && persona.getApellidos() != null
                    && persona.getApellidos().toLowerCase().contains(normalized);
            boolean matchesDocumento = persona != null && persona.getDocumento() != null
                    && persona.getDocumento().toLowerCase().contains(normalized);
            boolean matchesHangar = hangar != null && hangar.getCodigo() != null
                    && hangar.getCodigo().toLowerCase().contains(normalized);
            return matchesNombre || matchesApellido || matchesDocumento || matchesHangar;
        });
    }

    private void populatePersonas() {
        personas.setAll(personaService.findAll());
        personas.sort(Comparator.comparing(persona -> persona.getNombres() != null ? persona.getNombres() : "",
                String.CASE_INSENSITIVE_ORDER));
        if (personaCombo == null) {
            return;
        }
        personaCombo.setItems(personas);
        personaCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Persona item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombres() + " " + item.getApellidos() + " (" + item.getDocumento() + ")");
                }
            }
        });
        personaCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Persona item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombres() + " " + item.getApellidos());
                }
            }
        });
    }

    private void populateHangares() {
        hangares.setAll(hangarService.findAll());
        hangares.sort(Comparator.comparing(hangar -> hangar.getCodigo() != null ? hangar.getCodigo() : "",
                String.CASE_INSENSITIVE_ORDER));
        if (hangarCombo == null) {
            return;
        }
        hangarCombo.setItems(hangares);
        hangarCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Hangar item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCodigo());
                }
            }
        });
        hangarCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Hangar item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCodigo());
                }
            }
        });
    }

    private boolean isFormValid() {
        return personaCombo != null && personaCombo.getValue() != null
                && hangarCombo != null && hangarCombo.getValue() != null;
    }

    private void refreshTable() {
        encargados.setAll(encargadoService.findAll());
        if (encargadoTable != null) {
            encargadoTable.refresh();
        }
        onBuscar();
    }

    private void fillForm(Encargado encargado) {
        if (encargado == null) {
            clearForm();
            return;
        }
        if (personaCombo != null && encargado.getPersona() != null) {
            personaCombo.getSelectionModel().select(findPersonaById(encargado.getPersona().getId()));
        }
        if (hangarCombo != null && encargado.getHangar() != null) {
            hangarCombo.getSelectionModel().select(findHangarById(encargado.getHangar().getId()));
        }
    }

    private Persona findPersonaById(Long id) {
        return personas.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    private Hangar findHangarById(Long id) {
        return hangares.stream().filter(h -> h.getId().equals(id)).findFirst().orElse(null);
    }

    private void clearForm() {
        if (personaCombo != null) {
            personaCombo.getSelectionModel().clearSelection();
        }
        if (hangarCombo != null) {
            hangarCombo.getSelectionModel().clearSelection();
        }
    }

    private boolean isDifferentRecord(Encargado current, Encargado existing) {
        return current.getId() == null || !current.getId().equals(existing.getId());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
