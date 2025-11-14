package com.example.hangar.repository;

import com.example.hangar.model.Hangar;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HangarRepository extends JpaRepository<Hangar, Long> {

    @EntityGraph(attributePaths = "empresa")
    @Query("SELECT h FROM Hangar h")
    List<Hangar> findAllWithEmpresa();

    @EntityGraph(attributePaths = {"naves", "talleres", "encargado"})
    @Query("SELECT h FROM Hangar h WHERE h.id = ?1")
    Optional<Hangar> findByIdWithAssociations(Long id);
}