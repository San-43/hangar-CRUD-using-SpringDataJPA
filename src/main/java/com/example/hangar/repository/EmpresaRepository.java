package com.example.hangar.repository;

import com.example.hangar.model.Empresa;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @EntityGraph(attributePaths = {"hangares", "naves"})
    @Query("SELECT e FROM Empresa e WHERE e.id = ?1")
    Optional<Empresa> findByIdWithAssociations(Long id);
}
