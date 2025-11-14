package com.example.hangar.service;

import com.example.hangar.model.Modelo;
import com.example.hangar.repository.ModeloRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ModeloServiceImpl implements ModeloService {

    private final ModeloRepository repository;

    public ModeloServiceImpl(ModeloRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Modelo> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Modelo findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Modelo " + id + " no existe"));
    }

    @Override
    public Modelo save(Modelo entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Modelo existing = findById(id);
        repository.delete(existing);
    }
}
