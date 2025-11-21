-- Agregar columna nombre_modelo única en modelos
ALTER TABLE modelos ADD COLUMN nombre_modelo VARCHAR(150) NOT NULL UNIQUE AFTER id_modelo;
USE hangar;

