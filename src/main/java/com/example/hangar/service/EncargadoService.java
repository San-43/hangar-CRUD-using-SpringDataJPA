package com.example.hangar.service;

import com.example.hangar.model.Encargado;

import java.util.List;

public interface EncargadoService {

    List<Encargado> findAll();

    Encargado findById(Integer id);

    Encargado save(Encargado entity);

    void delete(Integer id);
}
