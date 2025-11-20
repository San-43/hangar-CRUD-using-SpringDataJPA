package com.example.hangar.service;

import com.example.hangar.model.Vuelo;

import java.util.List;

public interface VueloService {

    List<Vuelo> findAll();

    Vuelo findById(Integer id);

    Vuelo save(Vuelo entity);

    void delete(Integer id);
}
