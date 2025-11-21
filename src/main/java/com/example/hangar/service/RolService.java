package com.example.hangar.service;

import com.example.hangar.model.Rol;

import java.util.List;

public interface RolService {

    List<Rol> findAll();

    Rol findById(Integer id);

    Rol save(Rol entity);

    void delete(Integer id);

    String checkDeletionConstraints(Integer id);
}
