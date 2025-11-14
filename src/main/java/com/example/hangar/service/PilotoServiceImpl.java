package com.example.hangar.service;

import com.example.hangar.model.Piloto;
import com.example.hangar.repository.EncargadoRepository;
import com.example.hangar.repository.PilotoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PilotoServiceImpl implements PilotoService {

    private final PilotoRepository repository;
    private final EncargadoRepository encargadoRepository;

    public PilotoServiceImpl(PilotoRepository repository, EncargadoRepository encargadoRepository) {
        this.repository = repository;
        this.encargadoRepository = encargadoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Piloto> findAll() {
        return repository.findAllWithRolAndTripulaciones();
    }

    @Override
    @Transactional(readOnly = true)
    public Piloto findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Piloto " + id + " no existe"));
    }

    @Override
    public Piloto save(Piloto entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Piloto existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public String checkDeletionConstraints(Long id) {
        Piloto piloto = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new EntityNotFoundException("Piloto " + id + " no existe"));

        StringBuilder asociaciones = new StringBuilder();
        int totalAsociaciones = 0;

        if (piloto.getTripulaciones() != null && !piloto.getTripulaciones().isEmpty()) {
            asociaciones.append("- ").append(piloto.getTripulaciones().size()).append(" tripulación(es)\n");
            totalAsociaciones += piloto.getTripulaciones().size();
        }

        // Verificar si el piloto es un encargado
        if (encargadoRepository.findByPersona_Id(id).isPresent()) {
            asociaciones.append("- Es un encargado de hangar\n");
            totalAsociaciones++;
        }

        if (totalAsociaciones > 0) {
            return "Este piloto tiene los siguientes registros asociados:\n" + asociaciones +
                   "\nPrimero debe eliminar o reasignar los registros relacionados.";
        }

        return null; // No hay restricciones
    }
}
