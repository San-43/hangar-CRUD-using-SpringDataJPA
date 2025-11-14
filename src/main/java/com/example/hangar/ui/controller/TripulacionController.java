package com.example.hangar.ui.controller;

import com.example.hangar.model.Tripulacion;
import com.example.hangar.service.TripulacionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.springframework.stereotype.Component;

@Component
public class TripulacionController {

    private final TripulacionService tripulacionService;
    private final ObservableList<String> nombres = FXCollections.observableArrayList();

    public TripulacionController(TripulacionService tripulacionService) {
        this.tripulacionService = tripulacionService;
    }

    @FXML
    private ListView<String> tripulacionesList;

    @FXML
    public void initialize() {
        if (tripulacionesList != null) {
            tripulacionService.findAll()
                    .forEach(tripulacion -> nombres.add(tripulacion.getNombre()));
            tripulacionesList.setItems(nombres);
        }
    }
}
