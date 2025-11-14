package com.example.hangar.service;

import com.example.hangar.model.Empresa;

import java.util.List;

public interface EmpresaService {

    List<Empresa> findAll();

    Empresa findById(Long id);

    Empresa save(Empresa entity);

    void delete(Long id);
}
