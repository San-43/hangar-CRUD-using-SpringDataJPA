package com.example.hangar.repository;

import com.example.hangar.model.Taller;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TallerRepository extends JpaRepository<Taller, Long> {

    @Override
    @EntityGraph(attributePaths = {"hangar", "reportes"})
    List<Taller> findAll();
}
