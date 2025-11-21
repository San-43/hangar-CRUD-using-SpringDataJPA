-- ============================================
-- RESTRICCIÓN: Una persona = Un rol por vuelo
-- Fecha: 20 de Noviembre, 2025
-- ============================================

USE hangar;

-- Agregar restricción UNIQUE para asegurar que una persona
-- solo pueda tener un rol en un vuelo específico
ALTER TABLE tripulaciones
ADD CONSTRAINT uk_vuelo_persona UNIQUE (id_vuelo, id_persona);

-- Verificar la restricción
SHOW CREATE TABLE tripulaciones;

-- Consulta de validación
SELECT
    id_vuelo,
    id_persona,
    COUNT(*) as roles_por_persona
FROM tripulaciones
GROUP BY id_vuelo, id_persona
HAVING COUNT(*) > 1;

-- Si la consulta anterior devuelve filas, hay personas con múltiples roles
-- en el mismo vuelo (error de datos)

COMMIT;

