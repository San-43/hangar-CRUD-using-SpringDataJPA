package com.example.hangar.repository;

import com.example.hangar.model.Rol;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    @Override
    @EntityGraph(attributePaths = {"persona"})
    List<Rol> findAll();

    @Override
    @EntityGraph(attributePaths = {"persona"})
    Optional<Rol> findById(Integer id);
}
