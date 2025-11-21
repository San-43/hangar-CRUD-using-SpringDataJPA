-- ============================================
-- MIGRACIÓN: Agregar restricción UNIQUE para encargado en talleres
-- ============================================
-- Fecha: 2024-11-20
-- Objetivo: Garantizar que un encargado solo pueda estar asignado a un taller
-- ============================================

USE hangar;

-- Verificar si ya existe la restricción
SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = 'hangar'
  AND TABLE_NAME = 'talleres'
  AND CONSTRAINT_TYPE = 'UNIQUE';

-- Agregar restricción UNIQUE en id_encargado
-- Esto garantiza que un encargado solo puede estar en un taller
ALTER TABLE talleres
ADD CONSTRAINT uk_encargado_unico UNIQUE (id_encargado);

-- Verificar la restricción agregada
SHOW CREATE TABLE talleres;

-- Consultar talleres con sus encargados
SELECT
    t.id_taller,
    h.descripcion AS hangar,
    e.nombre AS encargado
FROM talleres t
LEFT JOIN hangares h ON t.id_hangar = h.id_hangar
LEFT JOIN encargados e ON t.id_encargado = e.id_encargado
ORDER BY t.id_taller;

-- ============================================
-- NOTAS IMPORTANTES:
-- ============================================
-- 1. Esta restricción UNIQUE garantiza que cada id_encargado solo aparezca una vez en la tabla talleres
-- 2. Un encargado solo puede estar asignado a UN taller
-- 3. Si intentas asignar el mismo encargado a otro taller, la BD rechazará la operación
-- 4. La validación en TallerServiceImpl proporciona un mensaje amigable antes de que llegue a la BD
-- ============================================

