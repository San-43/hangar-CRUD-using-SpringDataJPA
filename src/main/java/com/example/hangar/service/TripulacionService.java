package com.example.hangar.service;

import com.example.hangar.model.Tripulacion;

import java.util.List;

public interface TripulacionService {

    List<Tripulacion> findAll();

    Tripulacion findById(Integer id);

    Tripulacion save(Tripulacion entity);

    void delete(Integer id);

    String checkDeletionConstraints(Integer id);

    /**
     * Valida que una persona no tenga múltiples roles en el mismo vuelo
     */
    void validarPersonaUnicaPorVuelo(Integer idVuelo, Integer idPersona, Integer idTripulacionActual);

    /**
     * Valida la composición completa de la tripulación de un vuelo
     */
    void validarComposicionTripulacion(Integer idVuelo);
}
