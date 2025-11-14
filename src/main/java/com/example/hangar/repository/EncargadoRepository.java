package com.example.hangar.repository;

import com.example.hangar.model.Encargado;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EncargadoRepository extends JpaRepository<Encargado, Long> {

    @EntityGraph(attributePaths = {"persona", "hangar", "hangar.empresa"})
    @Query("SELECT e FROM Encargado e")
    List<Encargado> findAllWithRelations();

    Optional<Encargado> findByHangar_Id(Long hangarId);

    Optional<Encargado> findByPersona_Id(Long personaId);
}
