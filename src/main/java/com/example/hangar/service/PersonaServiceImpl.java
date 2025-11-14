package com.example.hangar.service;

import com.example.hangar.model.Persona;
import com.example.hangar.repository.EncargadoRepository;
import com.example.hangar.repository.PersonaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository repository;
    private final EncargadoRepository encargadoRepository;

    public PersonaServiceImpl(PersonaRepository repository, EncargadoRepository encargadoRepository) {
        this.repository = repository;
        this.encargadoRepository = encargadoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Persona> findAll() {
        return repository.findAllWithRolAndTripulaciones();
    }

    @Override
    @Transactional(readOnly = true)
    public Persona findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Persona " + id + " no existe"));
    }

    @Override
    public Persona save(Persona entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Persona existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public String checkDeletionConstraints(Long id) {
        Persona persona = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new EntityNotFoundException("Persona " + id + " no existe"));

        StringBuilder asociaciones = new StringBuilder();
        int totalAsociaciones = 0;

        if (persona.getTripulaciones() != null && !persona.getTripulaciones().isEmpty()) {
            asociaciones.append("- ").append(persona.getTripulaciones().size()).append(" tripulación(es)\n");
            totalAsociaciones += persona.getTripulaciones().size();
        }

        // Verificar si la persona es un encargado
        if (encargadoRepository.findByPersona_Id(id).isPresent()) {
            asociaciones.append("- Es un encargado de hangar\n");
            totalAsociaciones++;
        }

        if (totalAsociaciones > 0) {
            return "Esta persona tiene los siguientes registros asociados:\n" + asociaciones +
                   "\nPrimero debe eliminar o reasignar los registros relacionados.";
        }

        return null; // No hay restricciones
    }
}
