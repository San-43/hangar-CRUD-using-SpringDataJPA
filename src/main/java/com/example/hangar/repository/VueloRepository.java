package com.example.hangar.repository;

import com.example.hangar.model.Vuelo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VueloRepository extends JpaRepository<Vuelo, Long> {

    @Override
    @EntityGraph(attributePaths = {"nave", "tripulacion"})
    List<Vuelo> findAll();

    @Override
    @EntityGraph(attributePaths = {"nave", "tripulacion"})
    Optional<Vuelo> findById(Long id);
}
