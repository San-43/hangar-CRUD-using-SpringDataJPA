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

    @Override
    @Transactional(readOnly = true)
    public String checkDeletionConstraints(Long id) {
        Empresa empresa = repository.findByIdWithAssociations(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa " + id + " no existe"));

        StringBuilder asociaciones = new StringBuilder();
        int totalAsociaciones = 0;

        if (empresa.getHangares() != null && !empresa.getHangares().isEmpty()) {
            asociaciones.append("- ").append(empresa.getHangares().size()).append(" hangar(es)\n");
            totalAsociaciones += empresa.getHangares().size();
        }

        if (empresa.getNaves() != null && !empresa.getNaves().isEmpty()) {
            asociaciones.append("- ").append(empresa.getNaves().size()).append(" nave(s)\n");
            totalAsociaciones += empresa.getNaves().size();
        }

        if (totalAsociaciones > 0) {
            return "Esta empresa tiene los siguientes registros asociados:\n" + asociaciones +
                   "\nPrimero debe eliminar o reasignar los registros relacionados.";
        }

        return null; // No hay restricciones
    }
}
