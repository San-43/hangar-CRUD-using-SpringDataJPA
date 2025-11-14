package com.example.hangar.service;

import com.example.hangar.model.Encargado;
import com.example.hangar.repository.EncargadoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EncargadoService {

    private final EncargadoRepository repository;

    public EncargadoService(EncargadoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Encargado> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Encargado findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encargado " + id + " no existe"));
    }

    public Encargado save(Encargado entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Encargado existing = findById(id);
        repository.delete(existing);
    }
}
