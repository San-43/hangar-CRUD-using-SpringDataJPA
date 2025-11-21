-- Eliminar columna peso duplicada en naves (peso viene del modelo)
USE hangar;
ALTER TABLE naves DROP COLUMN peso;

