package com.example.hangar.service;

import com.example.hangar.model.Taller;

import java.util.List;

public interface TallerService {

    List<Taller> findAll();

    Taller findById(Integer id);

    Taller save(Taller entity);

    void delete(Integer id);

    String checkDeletionConstraints(Integer id);

    void validarEncargadoUnico(Integer idEncargado, Integer idTallerActual);
}
