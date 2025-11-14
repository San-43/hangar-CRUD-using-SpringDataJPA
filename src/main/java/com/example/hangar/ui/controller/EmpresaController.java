package com.example.hangar.ui.controller;

import com.example.hangar.model.Empresa;
import com.example.hangar.service.EmpresaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

@Component
public class EmpresaController {

    private final EmpresaService empresaService;
    private final ObservableList<Empresa> empresas = FXCollections.observableArrayList();

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @FXML
    private TableView<Empresa> empresaTable;

    @FXML
    private TableColumn<Empresa, String> nombreColumn;

    @FXML
    private TableColumn<Empresa, String> paisColumn;

    @FXML
    public void initialize() {
        if (empresaTable != null) {
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            paisColumn.setCellValueFactory(new PropertyValueFactory<>("pais"));
            empresas.setAll(empresaService.findAll());
            empresaTable.setItems(empresas);
        }
    }
}
