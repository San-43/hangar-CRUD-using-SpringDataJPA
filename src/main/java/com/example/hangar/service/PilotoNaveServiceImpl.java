package com.example.hangar.service;

import com.example.hangar.model.PilotoNave;
import com.example.hangar.repository.PilotoNaveRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PilotoNaveServiceImpl implements PilotoNaveService {

    private final PilotoNaveRepository repository;

    public PilotoNaveServiceImpl(PilotoNaveRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PilotoNave> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PilotoNave findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PilotoNave " + id + " no existe"));
    }

    @Override
    public PilotoNave save(PilotoNave entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        PilotoNave existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PilotoNave> findByPilotoId(Integer idPiloto) {
        return repository.findByPiloto_IdPiloto(idPiloto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PilotoNave> findByNaveId(Integer idNave) {
        return repository.findByNave_IdNave(idNave);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PilotoNave> findByEstado(String estado) {
        return repository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPilotoAndNave(Integer idPiloto, Integer idNave) {
        return repository.existsByPiloto_IdPilotoAndNave_IdNave(idPiloto, idNave);
    }
}

