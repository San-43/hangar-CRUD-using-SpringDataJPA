package com.example.hangar.repository;

import com.example.hangar.model.Nave;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NaveRepository extends JpaRepository<Nave, Long> {

    @Override
    @EntityGraph(attributePaths = {"modelo", "empresa", "hangar"})
    List<Nave> findAll();
}
