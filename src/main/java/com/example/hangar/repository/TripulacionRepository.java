package com.example.hangar.repository;

import com.example.hangar.model.Tripulacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TripulacionRepository extends JpaRepository<Tripulacion, Integer> {
}
