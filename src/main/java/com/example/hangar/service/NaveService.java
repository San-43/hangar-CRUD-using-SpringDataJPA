package com.example.hangar.service;

import com.example.hangar.model.Nave;

import java.util.List;

public interface NaveService {

    List<Nave> findAll();

    Nave findById(Long id);

    Nave save(Nave entity);

    void delete(Long id);
}
