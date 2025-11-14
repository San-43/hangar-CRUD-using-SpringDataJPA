package com.example.hangar.service;

import com.example.hangar.model.Nave;
import com.example.hangar.repository.NaveRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NaveService {

    private final NaveRepository repository;

    public NaveService(NaveRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Nave> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Nave findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nave " + id + " no existe"));
    }

    public Nave save(Nave entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Nave existing = findById(id);
        repository.delete(existing);
    }
}
