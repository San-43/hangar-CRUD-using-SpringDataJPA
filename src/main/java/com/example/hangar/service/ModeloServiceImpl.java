package com.example.hangar.service;

import com.example.hangar.model.Modelo;
import com.example.hangar.repository.ModeloRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ModeloServiceImpl implements ModeloService {

    private final ModeloRepository repository;

    public ModeloServiceImpl(ModeloRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Modelo> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Modelo findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Modelo " + id + " no existe"));
    }

    @Override
    public Modelo save(Modelo entity) {
        if (entity.getNombreModelo() == null || entity.getNombreModelo().isBlank()) {
            throw new IllegalArgumentException("El nombre del modelo es obligatorio");
        }
        // Validar unicidad (si es nuevo o cambia el nombre)
        boolean nombreExiste = repository.existsByNombreModeloIgnoreCase(entity.getNombreModelo());
        if (nombreExiste && (entity.getIdModelo() == null)) {
            throw new IllegalArgumentException("Ya existe un modelo con ese nombre");
        }
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Modelo existing = findById(id);
        repository.delete(existing);
    }
}
