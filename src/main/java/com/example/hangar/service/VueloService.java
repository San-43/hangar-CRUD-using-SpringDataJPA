package com.example.hangar.service;

import com.example.hangar.model.Vuelo;
import com.example.hangar.repository.VueloRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VueloService {

    private final VueloRepository repository;

    public VueloService(VueloRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Vuelo> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Vuelo findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vuelo " + id + " no existe"));
    }

    public Vuelo save(Vuelo entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Vuelo existing = findById(id);
        repository.delete(existing);
    }
}
