package com.example.hangar.repository;

import com.example.hangar.model.Persona;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    @EntityGraph(attributePaths = {"rol", "tripulaciones"})
    List<Persona> findAllWithRolAndTripulaciones();
}
