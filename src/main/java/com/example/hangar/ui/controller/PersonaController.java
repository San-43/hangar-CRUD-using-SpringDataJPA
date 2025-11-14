package com.example.hangar.ui.controller;

import com.example.hangar.model.Persona;
import com.example.hangar.service.PersonaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component
public class PersonaController {

    private final PersonaService personaService;
    private final ObservableList<Persona> personas = FXCollections.observableArrayList();

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @FXML
    private TableView<Persona> personaTable;

    @FXML
    private TableColumn<Persona, String> nombreColumn;

    @FXML
    private TableColumn<Persona, String> documentoColumn;

    @FXML
    public void initialize() {
        if (personaTable != null) {
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombres"));
            documentoColumn.setCellValueFactory(new PropertyValueFactory<>("documento"));
            personas.setAll(personaService.findAll());
            personaTable.setItems(personas);
        }
    }
}
