package com.example.hangar.service;

import com.example.hangar.model.Piloto;

import java.util.List;

public interface PilotoService {

    List<Piloto> findAll();

    Piloto findById(Integer id);

    Piloto save(Piloto entity);

    void delete(Integer id);
}
