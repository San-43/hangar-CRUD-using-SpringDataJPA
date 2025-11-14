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
    public Rol findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol " + id + " no existe"));
    }

    @Override
    public Rol save(Rol entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Rol existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public String checkDeletionConstraints(Long id) {
        findById(id); // Verificar que existe

        long personasCount = repository.countPersonasByRolId(id);

        if (personasCount > 0) {
            return "Este rol tiene " + personasCount + " persona(s) asociada(s).\n" +
                   "Primero debe eliminar o reasignar los registros relacionados.";
        }

        return null; // No hay restricciones
    }
}
