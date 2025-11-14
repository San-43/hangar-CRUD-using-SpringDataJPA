package com.example.hangar.repository;

import com.example.hangar.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RolRepository extends JpaRepository<Rol, Long> {

    @Query("SELECT COUNT(p) FROM Persona p WHERE p.rol.id = ?1")
    long countPersonasByRolId(Long rolId);
}
