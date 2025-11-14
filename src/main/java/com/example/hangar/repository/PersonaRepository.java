package com.example.hangar.repository;

import com.example.hangar.model.Persona;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    @EntityGraph(attributePaths = {"rol", "tripulaciones"})
    @Query("select p from Persona p")
    List<Persona> findAllWithRolAndTripulaciones();

    @EntityGraph(attributePaths = {"tripulaciones"})
    @Query("SELECT p FROM Persona p WHERE p.id = ?1")
    Optional<Persona> findByIdWithAssociations(Long id);
}
