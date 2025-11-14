package com.example.hangar.ui.controller;

import com.example.hangar.model.Vuelo;
import com.example.hangar.service.VueloService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component
public class VueloController {

    private final VueloService vueloService;
    private final ObservableList<Vuelo> vuelos = FXCollections.observableArrayList();

    public VueloController(VueloService vueloService) {
        this.vueloService = vueloService;
    }

    @FXML
    private TableView<Vuelo> vueloTable;

    @FXML
    private TableColumn<Vuelo, String> codigoColumn;

    @FXML
    private TableColumn<Vuelo, String> destinoColumn;

    @FXML
    public void initialize() {
        if (vueloTable != null) {
            codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
            destinoColumn.setCellValueFactory(new PropertyValueFactory<>("destino"));
            vuelos.setAll(vueloService.findAll());
            vueloTable.setItems(vuelos);
        }
    }
}
