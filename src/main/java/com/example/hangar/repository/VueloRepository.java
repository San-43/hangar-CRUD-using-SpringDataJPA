package com.example.hangar.repository;
import com.example.hangar.model.Vuelo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VueloRepository extends JpaRepository<Vuelo, Integer> {

    @Override
    @EntityGraph(attributePaths = {"nave", "nave.empresa", "nave.modelo"})
    List<Vuelo> findAll();

    @Override
    @EntityGraph(attributePaths = {"nave", "nave.empresa", "nave.modelo"})
    Optional<Vuelo> findById(Integer id);
}

