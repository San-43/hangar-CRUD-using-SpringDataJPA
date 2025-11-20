package com.example.hangar.repository;

import com.example.hangar.model.Persona;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PersonaRepository extends JpaRepository<Persona, Integer> {
}
