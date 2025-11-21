package com.example.hangar.service;

import com.example.hangar.model.Tripulacion;
import com.example.hangar.repository.TripulacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TripulacionServiceImpl implements TripulacionService {

    private final TripulacionRepository repository;

    public TripulacionServiceImpl(TripulacionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tripulacion> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Tripulacion findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tripulacion " + id + " no existe"));
    }

    @Override
    public Tripulacion save(Tripulacion entity) {
        // Validar antes de guardar
        if (entity.getVuelo() != null && entity.getPersona() != null) {
            validarPersonaUnicaPorVuelo(
                entity.getVuelo().getIdVuelo(),
                entity.getPersona().getIdPersona(),
                entity.getIdTripulacion()
            );
        }
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Tripulacion existing = findById(id);
        repository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public String checkDeletionConstraints(Integer id) {
        findById(id);
        // Las restricciones las maneja la base de datos con FK
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public void validarPersonaUnicaPorVuelo(Integer idVuelo, Integer idPersona, Integer idTripulacionActual) {
        List<Tripulacion> tripulaciones = repository.findByVuelo_IdVuelo(idVuelo);

        // Filtrar la tripulación actual si se está editando
        if (idTripulacionActual != null) {
            tripulaciones = tripulaciones.stream()
                .filter(t -> !t.getIdTripulacion().equals(idTripulacionActual))
                .collect(Collectors.toList());
        }

        // Verificar si la persona ya está asignada al vuelo
        boolean personaYaAsignada = tripulaciones.stream()
            .anyMatch(t -> t.getPersona() != null &&
                          t.getPersona().getIdPersona().equals(idPersona));

        if (personaYaAsignada) {
            throw new IllegalArgumentException(
                "Esta persona ya está asignada a este vuelo en otro rol. " +
                "Una persona solo puede tener un rol por vuelo."
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validarComposicionTripulacion(Integer idVuelo) {
        List<Tripulacion> tripulaciones = repository.findByVuelo_IdVuelo(idVuelo);

        if (tripulaciones.isEmpty()) {
            throw new IllegalArgumentException(
                "El vuelo no tiene tripulación asignada"
            );
        }

        // Contar roles
        Map<String, Long> conteoRoles = tripulaciones.stream()
            .filter(t -> t.getRolTripulacion() != null)
            .collect(Collectors.groupingBy(
                Tripulacion::getRolTripulacion,
                Collectors.counting()
            ));

        // Validar Capitán (exactamente 1)
        Long capitanes = conteoRoles.getOrDefault("Capitán", 0L);
        if (capitanes != 1) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener exactamente 1 Capitán. Encontrados: " + capitanes
            );
        }

        // Validar Copiloto (exactamente 1)
        Long copilotos = conteoRoles.getOrDefault("Copiloto", 0L);
        if (copilotos != 1) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener exactamente 1 Copiloto. Encontrados: " + copilotos
            );
        }

        // Validar Ingeniero de Vuelo (exactamente 1)
        Long ingenieros = conteoRoles.getOrDefault("Ingeniero de Vuelo", 0L);
        if (ingenieros != 1) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener exactamente 1 Ingeniero de Vuelo. Encontrados: " + ingenieros
            );
        }

        // Validar Auxiliares de Vuelo (mínimo 2)
        Long auxiliares = conteoRoles.getOrDefault("Auxiliar de Vuelo", 0L);
        if (auxiliares < 2) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener al menos 2 Auxiliares de Vuelo. Encontrados: " + auxiliares
            );
        }

        // Verificar total mínimo (1 capitán + 1 copiloto + 1 ingeniero + 2 auxiliares = 5)
        if (tripulaciones.size() < 5) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener al menos 5 tripulantes " +
                "(1 Capitán + 1 Copiloto + 1 Ingeniero + 2 Auxiliares). " +
                "Encontrados: " + tripulaciones.size()
            );
        }
    }
}
