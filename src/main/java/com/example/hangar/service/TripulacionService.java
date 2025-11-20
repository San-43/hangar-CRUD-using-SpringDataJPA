package com.example.hangar.service;

import com.example.hangar.model.Tripulacion;

import java.util.List;

public interface TripulacionService {

    List<Tripulacion> findAll();

    Tripulacion findById(Integer id);

    Tripulacion save(Tripulacion entity);

    void delete(Integer id);
}
