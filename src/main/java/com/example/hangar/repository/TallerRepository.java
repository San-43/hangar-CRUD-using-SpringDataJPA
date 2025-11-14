package com.example.hangar.repository;

import com.example.hangar.model.Taller;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TallerRepository extends JpaRepository<Taller, Long> {

    @Override
    @EntityGraph(attributePaths = {"hangar", "reportes"})
    List<Taller> findAll();

    @EntityGraph(attributePaths = {"reportes"})
    @Query("SELECT t FROM Taller t WHERE t.id = ?1")
    Optional<Taller> findByIdWithAssociations(Long id);
}
