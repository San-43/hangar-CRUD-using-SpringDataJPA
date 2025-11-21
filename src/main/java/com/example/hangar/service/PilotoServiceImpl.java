package com.example.hangar.service;

import com.example.hangar.model.Piloto;
import com.example.hangar.repository.PilotoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PilotoServiceImpl implements PilotoService {

    private final PilotoRepository repository;

    public PilotoServiceImpl(PilotoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Piloto> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Piloto findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Piloto " + id + " no existe"));
    }

    @Override
    public Piloto save(Piloto entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Piloto existing = findById(id);
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
