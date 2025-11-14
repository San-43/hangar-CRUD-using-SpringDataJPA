package com.example.hangar.util;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URL;

public class FxSpringIntegrator {

    private final ApplicationContext context;

    public FxSpringIntegrator(ApplicationContext context) {
        this.context = context;
    }

    public FXMLLoader loadView(URL viewUrl) throws IOException {
        FXMLLoader loader = new FXMLLoader(viewUrl);
        loader.setControllerFactory(context::getBean);
        loader.load();
        return loader;
    }
}
