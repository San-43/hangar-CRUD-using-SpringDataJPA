package com.example.hangar.service;

import com.example.hangar.model.Vuelo;

import java.util.List;

public interface VueloService {

    List<Vuelo> findAll();

    Vuelo findById(Long id);

    Vuelo save(Vuelo entity);

    void delete(Long id);
}
