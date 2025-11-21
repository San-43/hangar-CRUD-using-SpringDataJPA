package com.example.hangar.util;

import com.example.hangar.model.Tripulacion;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Validador centralizado para la composición de tripulación en vuelos.
 *
 * Requiere que un vuelo tenga:
 * - Exactamente 1 Capitán
 * - Exactamente 1 Copiloto
 * - Exactamente 1 Ingeniero de Vuelo
 * - 2 o más Auxiliares de Vuelo
 */
public class TripulacionValidator {

    public static class TripulacionValidationResult {
        public boolean isValid;
        public String message;
        public int capitanes;
        public int copilotos;
        public int ingenieros;
        public int auxiliares;

        public TripulacionValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
            this.capitanes = 0;
            this.copilotos = 0;
            this.ingenieros = 0;
            this.auxiliares = 0;
        }
    }

    /**
     * Valida la composición de tripulación y devuelve un resultado detallado.
     * No lanza excepciones, devuelve el resultado en un objeto.
     */
    public static TripulacionValidationResult validar(List<Tripulacion> tripulaciones) {
        TripulacionValidationResult result = new TripulacionValidationResult(true, "");

        if (tripulaciones == null || tripulaciones.isEmpty()) {
            result.isValid = false;
            result.message = "Un vuelo debe tener tripulación asignada";
            return result;
        }

        // Contar roles
        Map<String, Long> conteoRoles = tripulaciones.stream()
            .filter(t -> t.getRolTripulacion() != null)
            .collect(Collectors.groupingBy(
                Tripulacion::getRolTripulacion,
                Collectors.counting()
            ));

        result.capitanes = Math.toIntExact(conteoRoles.getOrDefault("Capitán", 0L));
        result.copilotos = Math.toIntExact(conteoRoles.getOrDefault("Copiloto", 0L));
        result.ingenieros = Math.toIntExact(conteoRoles.getOrDefault("Ingeniero de Vuelo", 0L));
        result.auxiliares = Math.toIntExact(conteoRoles.getOrDefault("Auxiliar de Vuelo", 0L));

        // Validar Capitán
        if (result.capitanes != 1) {
            result.isValid = false;
            result.message = "Se requiere exactamente 1 Capitán. Encontrados: " + result.capitanes;
            return result;
        }

        // Validar Copiloto
        if (result.copilotos != 1) {
            result.isValid = false;
            result.message = "Se requiere exactamente 1 Copiloto. Encontrados: " + result.copilotos;
            return result;
        }

        // Validar Ingeniero de Vuelo
        if (result.ingenieros != 1) {
            result.isValid = false;
            result.message = "Se requiere exactamente 1 Ingeniero de Vuelo. Encontrados: " + result.ingenieros;
            return result;
        }

        // Validar Auxiliares de Vuelo
        if (result.auxiliares < 2) {
            result.isValid = false;
            result.message = "Se requieren al menos 2 Auxiliares de Vuelo. Encontrados: " + result.auxiliares;
            return result;
        }

        // Validar que no haya personas duplicadas
        int personasUnicas = Math.toIntExact(tripulaciones.stream()
            .filter(t -> t.getPersona() != null)
            .map(t -> t.getPersona().getIdPersona())
            .distinct()
            .count());

        if (personasUnicas != tripulaciones.size()) {
            result.isValid = false;
            result.message = "No se puede asignar la misma persona a múltiples roles en el mismo vuelo";
            return result;
        }

        // Verificar total mínimo
        int totalMinimo = 5; // 1 + 1 + 1 + 2
        if (tripulaciones.size() < totalMinimo) {
            result.isValid = false;
            result.message = "Se requieren al menos " + totalMinimo + " tripulantes. Encontrados: " + tripulaciones.size();
            return result;
        }

        result.isValid = true;
        result.message = "Composición de tripulación válida. Total: " + tripulaciones.size() +
                       " (1 Capitán + 1 Copiloto + 1 Ingeniero + " + result.auxiliares + " Auxiliares)";
        return result;
    }

    /**
     * Devuelve un resumen texto de la composición actual de tripulación.
     */
    public static String obtenerResumen(List<Tripulacion> tripulaciones) {
        if (tripulaciones == null || tripulaciones.isEmpty()) {
            return "Sin tripulación asignada";
        }

        Map<String, Long> conteoRoles = tripulaciones.stream()
            .filter(t -> t.getRolTripulacion() != null)
            .collect(Collectors.groupingBy(
                Tripulacion::getRolTripulacion,
                Collectors.counting()
            ));

        long capitanes = conteoRoles.getOrDefault("Capitán", 0L);
        long copilotos = conteoRoles.getOrDefault("Copiloto", 0L);
        long ingenieros = conteoRoles.getOrDefault("Ingeniero de Vuelo", 0L);
        long auxiliares = conteoRoles.getOrDefault("Auxiliar de Vuelo", 0L);

        return String.format(
            "Tripulación: %d Capitán(es), %d Copiloto(s), %d Ingeniero(s), %d Auxiliar(es) - Total: %d",
            capitanes, copilotos, ingenieros, auxiliares, tripulaciones.size()
        );
    }
}

