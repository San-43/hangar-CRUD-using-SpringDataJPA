package com.example.hangar.service;

import com.example.hangar.model.Reporte;
import com.example.hangar.repository.ReporteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository repository;

    public ReporteServiceImpl(ReporteRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> findAll() {
        return repository.findAllWithRelations();
    }

    @Override
    @Transactional(readOnly = true)
    public Reporte findById(Integer id) {
        return repository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Reporte " + id + " no existe"));
    }

    @Override
    public Reporte save(Reporte entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Reporte existing = findById(id);
        repository.delete(existing);
    }
}
