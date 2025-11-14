package com.example.hangar.ui.controller;

import com.example.hangar.model.Nave;
import com.example.hangar.service.NaveService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component
public class NaveController {

    private final NaveService naveService;
    private final ObservableList<Nave> naves = FXCollections.observableArrayList();

    public NaveController(NaveService naveService) {
        this.naveService = naveService;
    }

    @FXML
    private TableView<Nave> naveTable;

    @FXML
    private TableColumn<Nave, String> matriculaColumn;

    @FXML
    private TableColumn<Nave, String> estadoColumn;

    @FXML
    public void initialize() {
        if (naveTable != null) {
            matriculaColumn.setCellValueFactory(new PropertyValueFactory<>("matricula"));
            estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
            naves.setAll(naveService.findAll());
            naveTable.setItems(naves);
        }
    }
}
