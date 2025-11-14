package com.example.hangar.service;

import com.example.hangar.model.Encargado;
import com.example.hangar.repository.EncargadoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EncargadoServiceImpl implements EncargadoService {

    private final EncargadoRepository repository;

    public EncargadoServiceImpl(EncargadoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encargado> findAll() {
        return repository.findAllWithRelations();
    }

    @Override
    @Transactional(readOnly = true)
    public Encargado findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encargado " + id + " no existe"));
    }

    @Override
    public Encargado save(Encargado entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Encargado existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Encargado> findByHangarId(Long hangarId) {
        return repository.findByHangar_Id(hangarId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Encargado> findByPersonaId(Long personaId) {
        return repository.findByPersona_Id(personaId);
    }
}
