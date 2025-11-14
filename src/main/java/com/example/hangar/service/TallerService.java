package com.example.hangar.service;

import com.example.hangar.model.Taller;
import com.example.hangar.repository.TallerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TallerService {

    private final TallerRepository repository;

    public TallerService(TallerRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Taller> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Taller findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Taller " + id + " no existe"));
    }

    public Taller save(Taller entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Taller existing = findById(id);
        repository.delete(existing);
    }
}
