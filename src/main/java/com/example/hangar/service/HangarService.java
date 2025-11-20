package com.example.hangar.service;

import com.example.hangar.model.Hangar;

import java.util.List;

public interface HangarService {

    List<Hangar> findAll();

    Hangar findById(Integer id);

    Hangar save(Hangar entity);

    void delete(Integer id);
}
