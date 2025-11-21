package com.example.hangar.repository;

import com.example.hangar.model.Tripulacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripulacionRepository extends JpaRepository<Tripulacion, Integer> {

    @Override
    @EntityGraph(attributePaths = {"vuelo", "persona"})
    List<Tripulacion> findAll();

    @Override
    @EntityGraph(attributePaths = {"vuelo", "persona"})
    Optional<Tripulacion> findById(Integer id);

    @EntityGraph(attributePaths = {"vuelo", "persona"})
    List<Tripulacion> findByVuelo_IdVuelo(Integer idVuelo);
}
