package com.example.hangar.repository;

import com.example.hangar.model.Tripulacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TripulacionRepository extends JpaRepository<Tripulacion, Long> {

    @EntityGraph(attributePaths = {"integrantes", "vuelos"})
    @Query("select t from Tripulacion t")
    List<Tripulacion> findAllWithIntegrantesAndVuelos();

    @EntityGraph(attributePaths = {"integrantes", "vuelos"})
    @Query("select t from Tripulacion t where t.id = :id")
    Optional<Tripulacion> findByIdWithIntegrantesAndVuelos(Long id);
}
