package com.example.hangar.service;

import com.example.hangar.model.Vuelo;
import com.example.hangar.repository.VueloRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VueloServiceImpl implements VueloService {

    private final VueloRepository repository;

    public VueloServiceImpl(VueloRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vuelo> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Vuelo findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vuelo " + id + " no existe"));
    }

    @Override
    public Vuelo save(Vuelo entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Vuelo existing = findById(id);
        repository.delete(existing);
    }
}
