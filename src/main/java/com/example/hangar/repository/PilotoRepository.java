package com.example.hangar.repository;

import com.example.hangar.model.Piloto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PilotoRepository extends JpaRepository<Piloto, Long> {

    @EntityGraph(attributePaths = {"rol", "tripulaciones"})
    List<Piloto> findAllWithRolAndTripulaciones();
}
