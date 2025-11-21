-- ============================================
-- CORRECCIÓN 1: Agregar campo 'licencia' a tabla personas
-- Fecha: 20 de Noviembre, 2025
-- Requerimiento: "información de las personas... nombre, licencia, horas de vuelo"
-- ============================================

USE hangar;

-- Agregar columna licencia
ALTER TABLE personas
ADD COLUMN licencia VARCHAR(50) AFTER hrs_vuelo;

-- Opcional: Agregar índice para búsquedas eficientes
CREATE INDEX idx_personas_licencia ON personas(licencia);

-- Actualizar datos de ejemplo (opcional - solo si ya tienes datos)
-- UPDATE personas SET licencia = 'PIL-COM-2020-001' WHERE id_persona = 1;
-- UPDATE personas SET licencia = 'PIL-COM-2018-042' WHERE id_persona = 2;
-- UPDATE personas SET licencia = 'TCP-2021-088' WHERE id_persona = 3;

-- Verificar cambios
DESCRIBE personas;

-- Consulta de validación
SELECT id_persona, nombre, licencia, hrs_vuelo
FROM personas
LIMIT 5;

COMMIT;

