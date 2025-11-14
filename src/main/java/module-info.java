module com.example.hangar {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;

    // Spring Boot + Data JPA
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires spring.data.jpa;
    requires spring.data.commons;
    requires spring.tx;
    requires spring.web;
    requires org.hibernate.orm.core;

    requires jakarta.persistence;
    requires jakarta.validation;

    // ----- exports (para que otros módulos vean las clases) -----
    exports com.example.hangar;
    exports com.example.hangar.config;
    exports com.example.hangar.model;
    exports com.example.hangar.repository;
    exports com.example.hangar.service;
    exports com.example.hangar.ui.controller;
    exports com.example.hangar.util;

    // ----- opens (para reflexión: Spring, Hibernate, FXML) -----

    // @SpringBootApplication, etc.
    opens com.example.hangar
            to spring.core, spring.beans, spring.context, javafx.fxml;

    // @Configuration (FxConfig, etc.)
    opens com.example.hangar.config
            to spring.core, spring.beans, spring.context;

    // Servicios (@Service) como EmpresaService
    opens com.example.hangar.service
            to spring.core, spring.beans, spring.context;

    // Entidades JPA
    opens com.example.hangar.model
            to org.hibernate.orm.core, spring.core, spring.beans;

    // Controladores JavaFX
    opens com.example.hangar.ui.controller
            to javafx.fxml, spring.core, spring.beans, spring.context;
}
