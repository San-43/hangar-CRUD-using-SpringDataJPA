package com.example.hangar.service;

import com.example.hangar.model.Tripulacion;
import com.example.hangar.repository.TripulacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TripulacionServiceImpl implements TripulacionService {

    private final TripulacionRepository repository;

    public TripulacionServiceImpl(TripulacionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tripulacion> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Tripulacion findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tripulacion " + id + " no existe"));
    }

    @Override
    public Tripulacion save(Tripulacion entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Tripulacion existing = findById(id);
        repository.delete(existing);
    }
}
