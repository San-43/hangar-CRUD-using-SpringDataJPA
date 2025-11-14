package com.example.hangar.service;

import com.example.hangar.model.Hangar;
import com.example.hangar.repository.HangarRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HangarService {

    private final HangarRepository repository;

    public HangarService(HangarRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Hangar> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Hangar findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hangar " + id + " no existe"));
    }

    public Hangar save(Hangar entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Hangar existing = findById(id);
        repository.delete(existing);
    }
}
