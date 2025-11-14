package com.example.hangar.service;

import com.example.hangar.model.Taller;

import java.util.List;

public interface TallerService {

    List<Taller> findAll();

    Taller findById(Long id);

    Taller save(Taller entity);

    void delete(Long id);

    String checkDeletionConstraints(Long id);
}
