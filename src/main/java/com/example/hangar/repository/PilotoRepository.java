package com.example.hangar.repository;

import com.example.hangar.model.Piloto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PilotoRepository extends JpaRepository<Piloto, Integer> {

    @Override
    @EntityGraph(attributePaths = {"persona", "navesCertificadas", "navesCertificadas.nave"})
    List<Piloto> findAll();

    @Override
    @EntityGraph(attributePaths = {"persona", "navesCertificadas", "navesCertificadas.nave"})
    Optional<Piloto> findById(Integer id);
}
