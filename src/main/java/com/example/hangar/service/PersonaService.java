package com.example.hangar.service;

import com.example.hangar.model.Persona;

import java.util.List;

public interface PersonaService {

    List<Persona> findAll();

    Persona findById(Integer id);

    Persona save(Persona entity);

    void delete(Integer id);

    String checkDeletionConstraints(Integer id);
}
