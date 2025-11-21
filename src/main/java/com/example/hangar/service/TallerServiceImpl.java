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
    public Taller findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Taller " + id + " no existe"));
    }

    @Override
    public Taller save(Taller entity) {
        // Validar que el encargado no esté asignado a otro taller
        if (entity.getEncargado() != null) {
            validarEncargadoUnico(
                entity.getEncargado().getIdEncargado(),
                entity.getIdTaller()
            );
        }
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Taller existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public String checkDeletionConstraints(Integer id) {
        Taller taller = findById(id);
        // Las restricciones las maneja la base de datos con FK
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public void validarEncargadoUnico(Integer idEncargado, Integer idTallerActual) {
        List<Taller> talleresConEsteEncargado = repository.findByEncargado_IdEncargado(idEncargado);

        // Filtrar el taller actual si se está editando
        if (idTallerActual != null) {
            talleresConEsteEncargado = talleresConEsteEncargado.stream()
                .filter(t -> !t.getIdTaller().equals(idTallerActual))
                .toList();
        }

        // Verificar si el encargado ya está asignado a otro taller
        if (!talleresConEsteEncargado.isEmpty()) {
            Taller tallerExistente = talleresConEsteEncargado.get(0);
            throw new IllegalArgumentException(
                "Este encargado ya está asignado al taller ID: " + tallerExistente.getIdTaller() +
                ". Un encargado solo puede estar asignado a un taller."
            );
        }
    }
}
