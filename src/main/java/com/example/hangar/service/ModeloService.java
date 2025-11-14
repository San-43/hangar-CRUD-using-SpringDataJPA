package com.example.hangar.service;

import com.example.hangar.model.Modelo;

import java.util.List;

public interface ModeloService {

    List<Modelo> findAll();

    Modelo findById(Long id);

    Modelo save(Modelo entity);

    void delete(Long id);
}
