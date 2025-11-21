package com.example.hangar.repository;

import com.example.hangar.model.Nave;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NaveRepository extends JpaRepository<Nave, Integer> {

    @Override
    @EntityGraph(attributePaths = {"modelo", "empresa", "hangar"})
    List<Nave> findAll();

    @Override
    @EntityGraph(attributePaths = {"modelo", "empresa", "hangar"})
    Optional<Nave> findById(Integer id);
}
