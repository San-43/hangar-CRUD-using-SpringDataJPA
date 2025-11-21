package com.example.hangar.repository;

import com.example.hangar.model.Taller;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TallerRepository extends JpaRepository<Taller, Integer> {

    @Override
    @EntityGraph(attributePaths = {"hangar", "encargado"})
    List<Taller> findAll();

    @Override
    @EntityGraph(attributePaths = {"hangar", "encargado"})
    Optional<Taller> findById(Integer id);

    @EntityGraph(attributePaths = {"hangar", "encargado"})
    List<Taller> findByEncargado_IdEncargado(Integer idEncargado);
}
