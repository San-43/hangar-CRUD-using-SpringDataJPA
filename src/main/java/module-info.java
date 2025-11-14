module com.example.hangar {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires spring.data.jpa;
    requires spring.tx;
    requires spring.web;
    requires org.hibernate.orm.core;

    requires jakarta.persistence;
    requires jakarta.validation;

    exports com.example.hangar;
    exports com.example.hangar.config;
    exports com.example.hangar.model;
    exports com.example.hangar.repository;
    exports com.example.hangar.service;
    exports com.example.hangar.ui.controller;
    exports com.example.hangar.util;

    opens com.example.hangar
            to spring.core, spring.beans, spring.context, javafx.fxml;

    opens com.example.hangar.model
            to org.hibernate.orm.core, spring.core, spring.beans;

    opens com.example.hangar.ui.controller
            to javafx.fxml, spring.core, spring.beans, spring.context;
}
