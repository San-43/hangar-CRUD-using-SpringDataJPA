package com.example.hangar.service;

import com.example.hangar.model.Hangar;
import com.example.hangar.repository.HangarRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HangarServiceImpl implements HangarService {

    private final HangarRepository repository;

    public HangarServiceImpl(HangarRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hangar> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Hangar findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hangar " + id + " no existe"));
    }

    @Override
    public Hangar save(Hangar entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Hangar existing = findById(id);
        repository.delete(existing);
    }
}
