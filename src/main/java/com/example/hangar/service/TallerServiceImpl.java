package com.example.hangar.service;

import com.example.hangar.model.Taller;
import com.example.hangar.repository.TallerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TallerServiceImpl implements TallerService {

    private final TallerRepository repository;

    public TallerServiceImpl(TallerRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Taller> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Taller findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Taller " + id + " no existe"));
    }

    @Override
    public Taller save(Taller entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Taller existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public String checkDeletionConstraints(Long id) {
        Taller taller = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new EntityNotFoundException("Taller " + id + " no existe"));

        if (taller.getReportes() != null && !taller.getReportes().isEmpty()) {
            return "Este taller tiene " + taller.getReportes().size() + " reporte(s) asociado(s).\n" +
                   "Primero debe eliminar o reasignar los registros relacionados.";
        }

        return null; // No hay restricciones
    }
}
