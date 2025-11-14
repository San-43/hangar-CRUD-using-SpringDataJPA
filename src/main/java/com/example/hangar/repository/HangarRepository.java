package com.example.hangar.repository;

import com.example.hangar.model.Hangar;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HangarRepository extends JpaRepository<Hangar, Long> {

    @EntityGraph(attributePaths = "empresa")
    @Query("SELECT h FROM Hangar h")
    List<Hangar> findAllWithEmpresa();
}