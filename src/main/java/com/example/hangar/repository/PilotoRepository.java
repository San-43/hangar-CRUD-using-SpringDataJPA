package com.example.hangar.repository;

import com.example.hangar.model.Piloto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PilotoRepository extends JpaRepository<Piloto, Long> {
}
