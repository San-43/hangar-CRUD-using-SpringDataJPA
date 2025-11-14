package com.example.hangar.service;

import com.example.hangar.model.Reporte;
import com.example.hangar.repository.ReporteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReporteService {

    private final ReporteRepository repository;

    public ReporteService(ReporteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Reporte> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Reporte findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reporte " + id + " no existe"));
    }

    public Reporte save(Reporte entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Reporte existing = findById(id);
        repository.delete(existing);
    }
}
