package com.example.hangar.service;

import com.example.hangar.model.Empresa;
import com.example.hangar.repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository repository;

    public EmpresaServiceImpl(EmpresaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empresa> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Empresa findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa " + id + " no existe"));
    }

    @Override
    public Empresa save(Empresa entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Empresa existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public String checkDeletionConstraints(Integer id) {
        Empresa empresa = findById(id);
        // Las restricciones las maneja la base de datos con FK
        return null;
    }
}

