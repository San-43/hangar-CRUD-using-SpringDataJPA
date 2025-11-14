package com.example.hangar.config;

import com.example.hangar.util.FxSpringIntegrator;
import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FxConfig {

    @Bean
    public FXMLLoader fxmlLoader(ApplicationContext context) {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        return loader;
    }

    @Bean
    public FxSpringIntegrator fxSpringIntegrator(ApplicationContext context) {
        return new FxSpringIntegrator(context);
    }
}
