-- ============================================
-- CORRECCIÓN 3: Validación de Composición de Tripulación
-- Fecha: 20 de Noviembre, 2025
-- Requerimiento: "2 pilotos (Capitán y Copiloto) + 1 ingeniero + 2+ auxiliares"
-- ============================================

USE hangar;

-- NOTA: La validación principal se implementa en la capa de servicio (VueloServiceImpl)
-- Este script crea triggers opcionales para validación adicional en base de datos

-- Crear tabla de auditoría de validaciones
CREATE TABLE IF NOT EXISTS tripulacion_validaciones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_vuelo INT NOT NULL,
    fecha_validacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    resultado ENUM('VALIDA', 'INVALIDA') NOT NULL,
    mensaje TEXT,
    FOREIGN KEY (id_vuelo) REFERENCES vuelos(id_vuelo) ON DELETE CASCADE
);

-- Función auxiliar para contar roles en un vuelo
DELIMITER $$

CREATE FUNCTION IF NOT EXISTS contar_rol_vuelo(
    p_id_vuelo INT,
    p_nombre_rol VARCHAR(100)
)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_count INT;

    SELECT COUNT(*)
    INTO v_count
    FROM tripulaciones t
    JOIN roles r ON t.id_rol = r.id_rol
    WHERE t.id_vuelo = p_id_vuelo
    AND r.rol = p_nombre_rol;

    RETURN v_count;
END$$

DELIMITER ;

-- Vista para auditoría de tripulaciones
CREATE OR REPLACE VIEW vista_tripulacion_auditoria AS
SELECT
    v.id_vuelo,
    v.origen,
    v.destino,
    v.fecha_salida,
    COUNT(t.id_tripulacion) as total_tripulantes,
    SUM(CASE WHEN r.rol = 'Capitán' THEN 1 ELSE 0 END) as total_capitanes,
    SUM(CASE WHEN r.rol = 'Copiloto' THEN 1 ELSE 0 END) as total_copilotos,
    SUM(CASE WHEN r.rol = 'Ingeniero de Vuelo' THEN 1 ELSE 0 END) as total_ingenieros,
    SUM(CASE WHEN r.rol = 'Auxiliar de Vuelo' THEN 1 ELSE 0 END) as total_auxiliares,
    CASE
        WHEN COUNT(t.id_tripulacion) < 5 THEN 'TRIPULACION_INCOMPLETA'
        WHEN SUM(CASE WHEN r.rol = 'Capitán' THEN 1 ELSE 0 END) != 1 THEN 'CAPITANES_INVALIDO'
        WHEN SUM(CASE WHEN r.rol = 'Copiloto' THEN 1 ELSE 0 END) != 1 THEN 'COPILOTOS_INVALIDO'
        WHEN SUM(CASE WHEN r.rol = 'Ingeniero de Vuelo' THEN 1 ELSE 0 END) != 1 THEN 'INGENIEROS_INVALIDO'
        WHEN SUM(CASE WHEN r.rol = 'Auxiliar de Vuelo' THEN 1 ELSE 0 END) < 2 THEN 'AUXILIARES_INSUFICIENTES'
        ELSE 'VALIDA'
    END as estado_validacion
FROM vuelos v
LEFT JOIN tripulaciones t ON v.id_vuelo = t.id_vuelo
LEFT JOIN roles r ON t.id_rol = r.id_rol
GROUP BY v.id_vuelo, v.origen, v.destino, v.fecha_salida;

-- Procedimiento para validar tripulación de un vuelo
DELIMITER $$

CREATE PROCEDURE IF NOT EXISTS validar_tripulacion_vuelo(
    IN p_id_vuelo INT,
    OUT p_es_valida BOOLEAN,
    OUT p_mensaje TEXT
)
BEGIN
    DECLARE v_total INT;
    DECLARE v_capitanes INT;
    DECLARE v_copilotos INT;
    DECLARE v_ingenieros INT;
    DECLARE v_auxiliares INT;

    -- Obtener conteos
    SELECT
        COUNT(*),
        SUM(CASE WHEN r.rol = 'Capitán' THEN 1 ELSE 0 END),
        SUM(CASE WHEN r.rol = 'Copiloto' THEN 1 ELSE 0 END),
        SUM(CASE WHEN r.rol = 'Ingeniero de Vuelo' THEN 1 ELSE 0 END),
        SUM(CASE WHEN r.rol = 'Auxiliar de Vuelo' THEN 1 ELSE 0 END)
    INTO
        v_total, v_capitanes, v_copilotos, v_ingenieros, v_auxiliares
    FROM tripulaciones t
    JOIN roles r ON t.id_rol = r.id_rol
    WHERE t.id_vuelo = p_id_vuelo;

    -- Validar
    SET p_es_valida = TRUE;
    SET p_mensaje = 'Tripulación válida';

    IF v_total < 5 THEN
        SET p_es_valida = FALSE;
        SET p_mensaje = CONCAT('Tripulación incompleta. Total: ', v_total, ' (mínimo 5)');
    ELSEIF v_capitanes != 1 THEN
        SET p_es_valida = FALSE;
        SET p_mensaje = CONCAT('Debe haber exactamente 1 Capitán. Encontrados: ', v_capitanes);
    ELSEIF v_copilotos != 1 THEN
        SET p_es_valida = FALSE;
        SET p_mensaje = CONCAT('Debe haber exactamente 1 Copiloto. Encontrados: ', v_copilotos);
    ELSEIF v_ingenieros != 1 THEN
        SET p_es_valida = FALSE;
        SET p_mensaje = CONCAT('Debe haber exactamente 1 Ingeniero de Vuelo. Encontrados: ', v_ingenieros);
    ELSEIF v_auxiliares < 2 THEN
        SET p_es_valida = FALSE;
        SET p_mensaje = CONCAT('Debe haber al menos 2 Auxiliares de Vuelo. Encontrados: ', v_auxiliares);
    END IF;

    -- Registrar auditoría
    INSERT INTO tripulacion_validaciones (id_vuelo, resultado, mensaje)
    VALUES (p_id_vuelo, IF(p_es_valida, 'VALIDA', 'INVALIDA'), p_mensaje);
END$$

DELIMITER ;

-- Consultas de verificación
SELECT '=== AUDITORÍA DE TRIPULACIONES ===' as '';

-- Mostrar estado de validación de todos los vuelos
SELECT * FROM vista_tripulacion_auditoria;

-- Mostrar vuelos con tripulación inválida
SELECT
    id_vuelo,
    origen,
    destino,
    estado_validacion,
    total_tripulantes,
    total_capitanes,
    total_copilotos,
    total_ingenieros,
    total_auxiliares
FROM vista_tripulacion_auditoria
WHERE estado_validacion != 'VALIDA';

-- Resumen de validaciones
SELECT
    estado_validacion,
    COUNT(*) as cantidad_vuelos
FROM vista_tripulacion_auditoria
GROUP BY estado_validacion;

COMMIT;

-- ============================================
-- NOTAS DE IMPLEMENTACIÓN:
-- ============================================
-- 1. La validación principal se hace en VueloServiceImpl.validarComposicionTripulacion()
-- 2. Los triggers y funciones aquí son OPCIONALES para validación adicional
-- 3. La vista vista_tripulacion_auditoria permite monitorear el estado de tripulaciones
-- 4. El procedimiento validar_tripulacion_vuelo puede llamarse manualmente para auditoría
--
-- Ejemplo de uso del procedimiento:
-- CALL validar_tripulacion_vuelo(1, @es_valida, @mensaje);
-- SELECT @es_valida as 'Es Válida', @mensaje as 'Mensaje';

