package com.example.hangar.service;

import com.example.hangar.model.Tripulacion;
import com.example.hangar.model.Vuelo;
import com.example.hangar.repository.TripulacionRepository;
import com.example.hangar.repository.VueloRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class VueloServiceImpl implements VueloService {

    private final VueloRepository repository;
    private final TripulacionRepository tripulacionRepository;

    public VueloServiceImpl(VueloRepository repository, TripulacionRepository tripulacionRepository) {
        this.repository = repository;
        this.tripulacionRepository = tripulacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vuelo> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Vuelo findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vuelo " + id + " no existe"));
    }

    @Override
    public Vuelo save(Vuelo entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        Vuelo existing = findById(id);
        repository.delete(existing);
    }

    @Override
    public void validarComposicionTripulacion(List<Tripulacion> tripulaciones) {
        if (tripulaciones == null || tripulaciones.isEmpty()) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener tripulación asignada"
            );
        }

        // Contar roles
        Map<String, Long> conteoRoles = tripulaciones.stream()
            .filter(t -> t.getRolTripulacion() != null)
            .collect(Collectors.groupingBy(
                Tripulacion::getRolTripulacion,
                Collectors.counting()
            ));

        // Validar capitán (exactamente 1)
        Long capitanes = conteoRoles.getOrDefault("Capitán", 0L);
        if (capitanes != 1) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener exactamente 1 Capitán. Encontrados: " + capitanes
            );
        }

        // Validar copiloto (exactamente 1)
        Long copilotos = conteoRoles.getOrDefault("Copiloto", 0L);
        if (copilotos != 1) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener exactamente 1 Copiloto. Encontrados: " + copilotos
            );
        }

        // Validar ingeniero de vuelo (exactamente 1)
        Long ingenieros = conteoRoles.getOrDefault("Ingeniero de Vuelo", 0L);
        if (ingenieros != 1) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener exactamente 1 Ingeniero de Vuelo. Encontrados: " + ingenieros
            );
        }

        // Validar auxiliares de vuelo (mínimo 2)
        Long auxiliares = conteoRoles.getOrDefault("Auxiliar de Vuelo", 0L);
        if (auxiliares < 2) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener al menos 2 Auxiliares de Vuelo. Encontrados: " + auxiliares
            );
        }

        // Validar que no haya personas duplicadas
        Set<Integer> personasIds = tripulaciones.stream()
            .filter(t -> t.getPersona() != null)
            .map(t -> t.getPersona().getIdPersona())
            .collect(Collectors.toSet());

        if (personasIds.size() != tripulaciones.size()) {
            throw new IllegalArgumentException(
                "No se puede asignar la misma persona a múltiples roles en el mismo vuelo"
            );
        }

        // Verificar total mínimo (1 capitán + 1 copiloto + 1 ingeniero + 2 auxiliares = 5)
        if (tripulaciones.size() < 5) {
            throw new IllegalArgumentException(
                "Un vuelo debe tener al menos 5 tripulantes (1 Capitán + 1 Copiloto + 1 Ingeniero + 2 Auxiliares). " +
                "Encontrados: " + tripulaciones.size()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validarTripulacionVuelo(Integer idVuelo) {
        // Verificar que el vuelo existe
        findById(idVuelo);

        // Obtener tripulación del vuelo
        List<Tripulacion> tripulaciones = tripulacionRepository.findByVuelo_IdVuelo(idVuelo);

        // Validar composición
        validarComposicionTripulacion(tripulaciones);
    }
}
