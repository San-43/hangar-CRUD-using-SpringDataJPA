package com.example.hangar.ui.controller;

import com.example.hangar.model.Piloto;
import com.example.hangar.model.Rol;
import com.example.hangar.service.PilotoService;
import com.example.hangar.service.RolService;
import javafx.beans.property.SimpleIntegerProperty;
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

@Component
public class PilotoController {

    private final PilotoService pilotoService;
    private final RolService rolService;
    private final ObservableList<Piloto> pilotos = FXCollections.observableArrayList();
    private final FilteredList<Piloto> filteredPilotos = new FilteredList<>(pilotos, piloto -> true);
    private final ObservableList<Rol> roles = FXCollections.observableArrayList();

    public PilotoController(PilotoService pilotoService, RolService rolService) {
        this.pilotoService = pilotoService;
        this.rolService = rolService;
    }

    @FXML
    private TableView<Piloto> pilotoTable;

    @FXML
    private TableColumn<Piloto, String> nombreColumn;

    @FXML
    private TableColumn<Piloto, String> apellidosColumn;

    @FXML
    private TableColumn<Piloto, String> documentoColumn;

    @FXML
    private TableColumn<Piloto, String> licenciaColumn;

    @FXML
    private TableColumn<Piloto, String> experienciaColumn;

    @FXML
    private TableColumn<Piloto, String> rolColumn;

    @FXML
    private TableColumn<Piloto, Number> tripulacionesColumn;

    @FXML
    private ComboBox<Rol> rolCombo;

    @FXML
    private TextField nombresField;

    @FXML
    private TextField apellidosField;

    @FXML
    private TextField documentoField;

    @FXML
    private TextField licenciaField;

    @FXML
    private TextField experienciaField;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        if (pilotoTable != null) {
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombres"));
            apellidosColumn.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
            documentoColumn.setCellValueFactory(new PropertyValueFactory<>("documento"));
            licenciaColumn.setCellValueFactory(new PropertyValueFactory<>("licencia"));
            experienciaColumn.setCellValueFactory(piloto -> new SimpleStringProperty(
                    piloto.getValue().getExperiencia() != null ? piloto.getValue().getExperiencia() : ""));
            rolColumn.setCellValueFactory(piloto -> new SimpleStringProperty(
                    piloto.getValue().getRol() != null ? piloto.getValue().getRol().getNombre() : ""));
            tripulacionesColumn.setCellValueFactory(piloto -> new SimpleIntegerProperty(
                    piloto.getValue().getTripulaciones() != null ? piloto.getValue().getTripulaciones().size() : 0));
            pilotos.setAll(pilotoService.findAll());
            pilotoTable.setItems(filteredPilotos);
            pilotoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillForm(newSel));
        }

        if (rolCombo != null) {
            roles.setAll(rolService.findAll());
            rolCombo.setItems(roles);
            rolCombo.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(Rol rol, boolean empty) {
                    super.updateItem(rol, empty);
                    setText(empty || rol == null ? null : rol.getNombre());
                }
            });
            rolCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Rol rol, boolean empty) {
                    super.updateItem(rol, empty);
                    setText(empty || rol == null ? null : rol.getNombre());
                }
            });
        }
    }

    @FXML
    private void onGuardar() {
        if (!isFormValid()) {
            showAlert(Alert.AlertType.WARNING, "Datos incompletos", "Todos los campos son obligatorios.");
            return;
        }
        Piloto selected = pilotoTable.getSelectionModel().getSelectedItem();
        Piloto piloto = selected != null ? pilotoService.findById(selected.getId()) : new Piloto();
        piloto.setNombres(nombresField.getText().trim());
        piloto.setApellidos(apellidosField.getText().trim());
        piloto.setDocumento(documentoField.getText().trim());
        piloto.setLicencia(licenciaField.getText().trim());
        piloto.setExperiencia(experienciaField.getText().trim());
        piloto.setRol(rolCombo.getValue());
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
        pilotoService.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Registro eliminado", "El piloto seleccionado fue eliminado.");
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
            boolean matchNombre = piloto.getNombres() != null && piloto.getNombres().toLowerCase().contains(normalized);
            boolean matchApellido = piloto.getApellidos() != null && piloto.getApellidos().toLowerCase().contains(normalized);
            boolean matchDocumento = piloto.getDocumento() != null && piloto.getDocumento().toLowerCase().contains(normalized);
            boolean matchLicencia = piloto.getLicencia() != null && piloto.getLicencia().toLowerCase().contains(normalized);
            boolean matchExperiencia = piloto.getExperiencia() != null && piloto.getExperiencia().toLowerCase().contains(normalized);
            return matchNombre || matchApellido || matchDocumento || matchLicencia || matchExperiencia;
        });
    }

    private boolean isFormValid() {
        return nombresField != null && !nombresField.getText().isBlank()
                && apellidosField != null && !apellidosField.getText().isBlank()
                && documentoField != null && !documentoField.getText().isBlank()
                && licenciaField != null && !licenciaField.getText().isBlank()
                && experienciaField != null && !experienciaField.getText().isBlank()
                && rolCombo != null && rolCombo.getValue() != null;
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
        nombresField.setText(piloto.getNombres());
        apellidosField.setText(piloto.getApellidos());
        documentoField.setText(piloto.getDocumento());
        licenciaField.setText(piloto.getLicencia());
        experienciaField.setText(piloto.getExperiencia());
        if (rolCombo != null && piloto.getRol() != null) {
            Rol rol = roles.stream()
                    .filter(r -> r.getId().equals(piloto.getRol().getId()))
                    .findFirst()
                    .orElse(null);
            rolCombo.getSelectionModel().select(rol);
        }
    }

    private void clearForm() {
        if (nombresField != null) {
            nombresField.clear();
        }
        if (apellidosField != null) {
            apellidosField.clear();
        }
        if (documentoField != null) {
            documentoField.clear();
        }
        if (licenciaField != null) {
            licenciaField.clear();
        }
        if (experienciaField != null) {
            experienciaField.clear();
        }
        if (rolCombo != null) {
            rolCombo.getSelectionModel().clearSelection();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
