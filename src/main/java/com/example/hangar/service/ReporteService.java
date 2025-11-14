package com.example.hangar.service;

import com.example.hangar.model.Reporte;

import java.util.List;

public interface ReporteService {

    List<Reporte> findAll();

    Reporte findById(Long id);

    Reporte save(Reporte entity);

    void delete(Long id);
}
