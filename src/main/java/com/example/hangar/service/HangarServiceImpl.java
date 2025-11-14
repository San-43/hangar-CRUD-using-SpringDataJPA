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
        return repository.findAllWithEmpresa();
    }

    @Override
    @Transactional(readOnly = true)
    public Hangar findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hangar " + id + " no existe"));
    }

    @Override
    public Hangar save(Hangar entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Hangar existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public String checkDeletionConstraints(Long id) {
        Hangar hangar = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new EntityNotFoundException("Hangar " + id + " no existe"));

        StringBuilder asociaciones = new StringBuilder();
        int totalAsociaciones = 0;

        if (hangar.getNaves() != null && !hangar.getNaves().isEmpty()) {
            asociaciones.append("- ").append(hangar.getNaves().size()).append(" nave(s)\n");
            totalAsociaciones += hangar.getNaves().size();
        }

        if (hangar.getTalleres() != null && !hangar.getTalleres().isEmpty()) {
            asociaciones.append("- ").append(hangar.getTalleres().size()).append(" taller(es)\n");
            totalAsociaciones += hangar.getTalleres().size();
        }

        if (hangar.getEncargado() != null) {
            asociaciones.append("- 1 encargado\n");
            totalAsociaciones++;
        }

        if (totalAsociaciones > 0) {
            return "Este hangar tiene los siguientes registros asociados:\n" + asociaciones +
                   "\nPrimero debe eliminar o reasignar los registros relacionados.";
        }

        return null; // No hay restricciones
    }
}
