package com.example.hangar.service;

import com.example.hangar.model.Rol;

import java.util.List;

public interface RolService {

    List<Rol> findAll();

    Rol findById(Long id);

    Rol save(Rol entity);

    void delete(Long id);

    String checkDeletionConstraints(Long id);
}
