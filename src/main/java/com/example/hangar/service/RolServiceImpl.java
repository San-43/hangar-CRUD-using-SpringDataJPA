package com.example.hangar.service;

import com.example.hangar.model.Rol;
import com.example.hangar.repository.RolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RolServiceImpl implements RolService {

    private final RolRepository repository;

    public RolServiceImpl(RolRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Rol findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol " + id + " no existe"));
    }

    @Override
    public Rol save(Rol entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Rol existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public String checkDeletionConstraints(Integer id) {
        findById(id);
        // Las restricciones las maneja la base de datos con FK
        return null;
    }
}
