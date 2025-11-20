package com.example.hangar.repository;

import com.example.hangar.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReporteRepository extends JpaRepository<Reporte, Integer> {

    @Query("SELECT r FROM Reporte r LEFT JOIN FETCH r.taller LEFT JOIN FETCH r.nave LEFT JOIN FETCH r.encargado")
    List<Reporte> findAllWithRelations();

    @Query("SELECT r FROM Reporte r LEFT JOIN FETCH r.taller LEFT JOIN FETCH r.nave LEFT JOIN FETCH r.encargado WHERE r.id = :id")
    Optional<Reporte> findByIdWithRelations(Integer id);
}
