package com.example.hangar.service;

import com.example.hangar.model.Rol;
import com.example.hangar.repository.RolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RolService {

    private final RolRepository repository;

    public RolService(RolRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Rol> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Rol findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol " + id + " no existe"));
    }

    public Rol save(Rol entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Rol existing = findById(id);
        repository.delete(existing);
    }
}
