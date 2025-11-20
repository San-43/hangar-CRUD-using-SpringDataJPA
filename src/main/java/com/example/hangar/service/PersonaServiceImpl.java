package com.example.hangar.service;

import com.example.hangar.model.Persona;
import com.example.hangar.repository.PersonaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository repository;

    public PersonaServiceImpl(PersonaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Persona> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Persona findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Persona " + id + " no existe"));
    }

    @Override
    public Persona save(Persona entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Persona existing = findById(id);
        repository.delete(existing);
    }
}
