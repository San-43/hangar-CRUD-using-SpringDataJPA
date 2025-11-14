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
        List<Empresa> empresas = repository.findAll();
        empresas.forEach(this::initializeAssociations);
        return empresas;
    }

    @Override
    @Transactional(readOnly = true)
    public Empresa findById(Long id) {
        Empresa empresa = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa " + id + " no existe"));
        initializeAssociations(empresa);
        return empresa;
    }

    @Override
    public Empresa save(Empresa entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        Empresa existing = findById(id);
        repository.delete(existing);
    }

    private void initializeAssociations(Empresa empresa) {
        if (empresa == null) {
            return;
        }
        empresa.getHangares().size();
        empresa.getNaves().size();
    }
}
