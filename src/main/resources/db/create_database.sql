
-- ============================================
-- CREAR BASE DE DATOS HANGAR
-- ============================================

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS hangar
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE hangar;

-- Verificar que se creó correctamente
SELECT 'Base de datos hangar creada exitosamente' AS mensaje;
SELECT DATABASE() AS base_de_datos_actual;
