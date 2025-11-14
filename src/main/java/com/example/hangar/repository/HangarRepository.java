package com.example.hangar.repository;

import com.example.hangar.model.Hangar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HangarRepository extends JpaRepository<Hangar, Long> {
}
