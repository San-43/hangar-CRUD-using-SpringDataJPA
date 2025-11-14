package com.example.hangar.repository;

import com.example.hangar.model.Nave;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaveRepository extends JpaRepository<Nave, Long> {
}
