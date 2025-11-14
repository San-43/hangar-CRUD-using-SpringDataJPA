package com.example.hangar.ui.controller;

import com.example.hangar.util.FxSpringIntegrator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainController {

    private final FxSpringIntegrator fxSpringIntegrator;

    public MainController(FxSpringIntegrator fxSpringIntegrator) {
        this.fxSpringIntegrator = fxSpringIntegrator;
    }

    @FXML
    private Label welcomeLabel;

    @FXML
    private StackPane contentPane;

    @FXML
    public void initialize() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Panel principal del Hangar");
        }
    }

    @FXML
    private void showEmpresas() {
        loadView("/fxml/empresa-view.fxml");
    }

    @FXML
    private void showPilotos() {
        loadView("/fxml/piloto-view.fxml");
    }

    @FXML
    private void showTalleres() {
        loadView("/fxml/taller-view.fxml");
    }

    @FXML
    private void showTripulaciones() {
        loadView("/fxml/tripulacion-view.fxml");
    }

    @FXML
    private void showVuelos() {
        loadView("/fxml/vuelo-view.fxml");
    }

    @FXML
    private void showNaves() {
        loadView("/fxml/nave-view.fxml");
    }

    @FXML
    private void showPersonas() {
        loadView("/fxml/persona-view.fxml");
    }

    private void loadView(String resource) {
        if (contentPane == null) {
            return;
        }
        try {
            FXMLLoader loader = fxSpringIntegrator.loadView(getClass().getResource(resource));
            Node view = loader.getRoot();
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            showError("No se pudo cargar la vista solicitada.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al cargar vista");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
