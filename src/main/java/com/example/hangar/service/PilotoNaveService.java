package com.example.hangar.service;

import com.example.hangar.model.PilotoNave;
import java.util.List;

public interface PilotoNaveService {

    List<PilotoNave> findAll();

    PilotoNave findById(Integer id);

    PilotoNave save(PilotoNave entity);

    void delete(Integer id);

    List<PilotoNave> findByPilotoId(Integer idPiloto);

    List<PilotoNave> findByNaveId(Integer idNave);

    List<PilotoNave> findByEstado(String estado);

    boolean existsByPilotoAndNave(Integer idPiloto, Integer idNave);
}


