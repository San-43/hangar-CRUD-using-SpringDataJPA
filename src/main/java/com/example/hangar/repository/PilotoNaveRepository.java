package com.example.hangar.repository;

import com.example.hangar.model.PilotoNave;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PilotoNaveRepository extends JpaRepository<PilotoNave, Integer> {

    @EntityGraph(attributePaths = {"piloto", "piloto.persona", "nave", "nave.modelo"})
    List<PilotoNave> findByPiloto_IdPiloto(Integer idPiloto);

    @EntityGraph(attributePaths = {"piloto", "piloto.persona", "nave", "nave.modelo"})
    List<PilotoNave> findByNave_IdNave(Integer idNave);

    @EntityGraph(attributePaths = {"piloto", "piloto.persona", "nave", "nave.modelo"})
    List<PilotoNave> findByEstado(String estado);

    boolean existsByPiloto_IdPilotoAndNave_IdNave(Integer idPiloto, Integer idNave);

    @EntityGraph(attributePaths = {"piloto", "piloto.persona", "nave", "nave.modelo"})
    @Override
    List<PilotoNave> findAll();
}

