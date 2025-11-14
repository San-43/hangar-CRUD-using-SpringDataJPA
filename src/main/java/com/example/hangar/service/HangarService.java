package com.example.hangar.service;

import com.example.hangar.model.Hangar;

import java.util.List;

public interface HangarService {

    List<Hangar> findAll();

    Hangar findById(Long id);

    Hangar save(Hangar entity);

    void delete(Long id);
}
