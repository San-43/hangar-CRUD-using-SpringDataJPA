package com.example.hangar.repository;

import com.example.hangar.model.Piloto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PilotoRepository extends JpaRepository<Piloto, Long> {

    @EntityGraph(attributePaths = {"rol", "tripulaciones"})
    @Query("select p from Piloto p")
    List<Piloto> findAllWithRolAndTripulaciones();

    @EntityGraph(attributePaths = {"tripulaciones"})
    @Query("SELECT p FROM Piloto p WHERE p.id = ?1")
    Optional<Piloto> findByIdWithAssociations(Long id);
}
