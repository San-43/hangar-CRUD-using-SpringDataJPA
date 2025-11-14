package com.example.hangar.service;

import com.example.hangar.model.Piloto;
import com.example.hangar.repository.PilotoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PilotoService {

    private final PilotoRepository repository;

    public PilotoService(PilotoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Piloto> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Piloto findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Piloto " + id + " no existe"));
    }

    public Piloto save(Piloto entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Piloto existing = findById(id);
        repository.delete(existing);
    }
}
