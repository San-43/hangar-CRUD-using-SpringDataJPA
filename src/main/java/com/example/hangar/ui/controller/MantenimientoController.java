package com.example.hangar.ui.controller;

import com.example.hangar.service.ReporteService;
import com.example.hangar.service.TallerService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class MantenimientoController {

    private final ReporteService reporteService;
    private final TallerService tallerService;

    public MantenimientoController(ReporteService reporteService, TallerService tallerService) {
        this.reporteService = reporteService;
        this.tallerService = tallerService;
    }

    @FXML
    private Label resumenLabel;

    @FXML
    public void initialize() {
        if (resumenLabel != null) {
            resumenLabel.setText(String.format("Talleres registrados: %d | Reportes abiertos: %d",
                    tallerService.findAll().size(),
                    reporteService.findAll().size()));
        }
    }
}
