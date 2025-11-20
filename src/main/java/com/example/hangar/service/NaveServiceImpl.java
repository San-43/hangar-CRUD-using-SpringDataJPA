package com.example.hangar.service;

import com.example.hangar.model.Nave;
import com.example.hangar.repository.NaveRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NaveServiceImpl implements NaveService {

    private final NaveRepository repository;

    public NaveServiceImpl(NaveRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Nave> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Nave findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nave " + id + " no existe"));
    }

    @Override
    public Nave save(Nave entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Nave existing = findById(id);
        repository.delete(existing);
    }
}
