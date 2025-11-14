package com.example.hangar.service;

import com.example.hangar.model.Tripulacion;

import java.util.List;

public interface TripulacionService {

    List<Tripulacion> findAll();

    Tripulacion findById(Long id);

    Tripulacion save(Tripulacion entity);

    void delete(Long id);
}
