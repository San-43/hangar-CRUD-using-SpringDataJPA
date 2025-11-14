package com.example.hangar.service;

import com.example.hangar.model.Empresa;
import com.example.hangar.repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmpresaService {

    private final EmpresaRepository repository;

    public EmpresaService(EmpresaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Empresa> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Empresa findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa " + id + " no existe"));
    }

    public Empresa save(Empresa entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Empresa existing = findById(id);
        repository.delete(existing);
    }
}
