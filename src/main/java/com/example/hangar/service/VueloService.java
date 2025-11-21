package com.example.hangar.service;

import com.example.hangar.model.Tripulacion;
import com.example.hangar.model.Vuelo;

import java.util.List;

public interface VueloService {

    List<Vuelo> findAll();

    Vuelo findById(Integer id);

    Vuelo save(Vuelo entity);

    void delete(Integer id);

    /**
     * Valida que la composición de tripulación cumpla con los requerimientos:
     * - 1 Capitán
     * - 1 Copiloto
     * - 1 Ingeniero de Vuelo
     * - 2 o más Auxiliares de Vuelo
     */
    void validarComposicionTripulacion(List<Tripulacion> tripulaciones);

    /**
     * Valida la tripulación de un vuelo existente
     */
    void validarTripulacionVuelo(Integer idVuelo);
}
