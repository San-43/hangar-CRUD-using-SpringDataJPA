package com.example.hangar.repository;

import com.example.hangar.model.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloRepository extends JpaRepository<Modelo, Integer> {
    boolean existsByNombreModeloIgnoreCase(String nombreModelo);
}
