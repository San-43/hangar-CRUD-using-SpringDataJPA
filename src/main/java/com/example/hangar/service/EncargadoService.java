package com.example.hangar.service;

import com.example.hangar.model.Encargado;

import java.util.List;
import java.util.Optional;

public interface EncargadoService {

    List<Encargado> findAll();

    Encargado findById(Long id);

    Encargado save(Encargado entity);

    void delete(Long id);

    Optional<Encargado> findByHangarId(Long hangarId);

    Optional<Encargado> findByPersonaId(Long personaId);
}
