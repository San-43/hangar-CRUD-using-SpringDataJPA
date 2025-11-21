package com.example.hangar.service;

import com.example.hangar.model.Encargado;
import com.example.hangar.repository.EncargadoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Encargado findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encargado " + id + " no existe"));
    }

    @Override
    public Encargado save(Encargado entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Encargado existing = findById(id);
        repository.delete(existing);
    }
}
