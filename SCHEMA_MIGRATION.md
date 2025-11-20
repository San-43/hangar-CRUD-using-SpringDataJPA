# Database Schema Migration Guide

## Overview
This document describes the migration from the old database schema to the new hangar database schema. The data layer (entities, repositories, and services) has been successfully updated. The UI layer (JavaFX controllers) requires additional updates to work with the new schema.

## Completed Changes

### 1. Entity Models
All entity models have been updated to match the new database schema:

#### EMPRESA
- **Old**: id (Long), nombre, pais
- **New**: id_empresa (Integer), nombre, contacto, ubicacion, rfc
- **Changes**: Removed `pais`, added `contacto`, `ubicacion`, `rfc`; ID type changed to Integer

#### ENCARGADO
- **Old**: id (Long), persona (OneToOne), hangar (OneToOne)
- **New**: id_encargado (Integer), nombre
- **Changes**: Simplified to standalone entity with just nombre; removed relationships to persona and hangar

#### HANGAR
- **Old**: id (Long), codigo, capacidad, ubicacion, empresa (ManyToOne)
- **New**: id_hangar (Integer), descripcion, capacidad, area, num
- **Changes**: Removed `codigo`, `ubicacion`, `empresa`; added `descripcion`, `area`, `num`

#### TALLER
- **Old**: id (Long), nombre, especialidad, hangar (ManyToOne)
- **New**: id_taller (Integer), id_hangar (ManyToOne), id_encargado (ManyToOne)
- **Changes**: Removed `nombre`, `especialidad`; added relationship to Encargado

#### MODELO
- **Old**: id (Long), nombre, fabricante, capacidad
- **New**: id_modelo (Integer), peso, pais_fabricacion
- **Changes**: Completely changed fields; removed `nombre`, `fabricante`, `capacidad`; added `peso`, `pais_fabricacion`

#### PERSONA
- **Old**: id (Long), nombres, apellidos, documento, rol (ManyToOne)
- **New**: id_persona (Integer), nombre, curp, edad, celular, hrs_vuelo
- **Changes**: Changed from `nombres`/`apellidos` to single `nombre`; replaced `documento` with `curp`; removed `rol` relationship; added `edad`, `celular`, `hrs_vuelo`

#### ROL
- **Old**: id (Long), nombre
- **New**: id_rol (Integer), id_persona (ManyToOne), rol
- **Changes**: Renamed `nombre` to `rol`; added relationship to Persona

#### PILOTO
- **Old**: id (Long), extends Persona, licencia, experiencia
- **New**: id_piloto (Integer), id_persona (ManyToOne), id_modelo (ManyToOne), hrs_voladas
- **Changes**: Changed from inheritance to composition; removed `licencia`, `experiencia`; added relationships to Persona and Modelo; added `hrs_voladas`

#### NAVE
- **Old**: id (Long), matricula, estado, modelo, empresa, hangar
- **New**: id_nave (Integer), id_empresa, id_hangar, id_modelo, peso, capacidad, estado
- **Changes**: Removed `matricula`; added `peso`, `capacidad`

#### VUELO
- **Old**: id (Long), codigo, destino, fechaSalida, nave, tripulacion
- **New**: id_vuelo (Integer), id_nave, origen, destino, fecha_salida, fecha_llegada, pasajeros, distancia
- **Changes**: Removed `codigo`, `tripulacion`; added `origen`, `fecha_llegada`, `pasajeros`, `distancia`

#### TRIPULACION
- **Old**: id (Long), nombre, integrantes (ManyToMany with Persona)
- **New**: id_tripulacion (Integer), id_vuelo (ManyToOne), id_persona (ManyToOne), id_rol (ManyToOne)
- **Changes**: Changed from standalone entity with many-to-many relationship to join entity; removed `nombre`, `integrantes`; added specific relationships to Vuelo, Persona, and Rol

#### REPORTE
- **Old**: id (Long), titulo, descripcion, fechaRegistro, taller, nave
- **New**: id_reporte (Integer), id_nave, id_taller, id_encargado, diagnostico, acciones_realizadas, fecha, costo
- **Changes**: Removed `titulo`; renamed `descripcion` to `diagnostico`; renamed `fechaRegistro` to `fecha`; added `id_encargado`, `acciones_realizadas`, `costo`

### 2. Repositories
All repository interfaces have been updated to use Integer IDs instead of Long. Removed custom query methods that referenced old schema relationships.

### 3. Services
All service interfaces and implementations have been updated to use Integer IDs and work with the new entity structure.

### 4. Build Configuration
- Updated Java version from 21 to 17 for environment compatibility
- Updated JavaFX version from 23.0.2 to 17.0.2 for Java 17 compatibility

## Pending Changes

### UI Controllers (JavaFX)
The JavaFX controllers in `src/main/java/com/example/hangar/ui/controller/` need to be updated to work with the new schema. This includes:

1. **Update field mappings**: Controllers reference old entity fields that no longer exist (e.g., `getNombres()`, `getApellidos()`, `getCodigo()`, `getMatricula()`, etc.)

2. **Update ID types**: Change all ID parameters from Long to Integer

3. **Update relationship handling**: 
   - Empresa no longer has `hangares` collection
   - Encargado no longer has `persona` and `hangar` relationships
   - Hangar no longer has `empresa` relationship
   - Persona no longer has `rol` relationship  
   - Piloto is no longer a subclass of Persona
   - Tripulacion is now a join entity, not a standalone entity with a many-to-many relationship

4. **Update form fields**: UI forms need to be updated to match new entity fields

### DataSeeder
The DataSeeder class (`src/main/java/com/example/hangar/config/DataSeeder.java`) has been disabled (commented out @Component annotation) because it uses the old schema. It needs to be rewritten to work with the new schema if sample data seeding is required.

## Database Migration
When deploying this code:

1. The application is configured with `spring.jpa.hibernate.ddl-auto=update` which will automatically update the database schema
2. **WARNING**: Data migration scripts may be needed if there is existing data in the database
3. Consider creating backup of existing data before deploying
4. Review and test the schema changes in a development environment first

## Next Steps

1. Update all JavaFX controllers to work with new entity structure
2. Update or rewrite DataSeeder for new schema (optional)
3. Update FXML files if they reference old fields
4. Test the application thoroughly with the new schema
5. Create data migration scripts if needed for production deployment

## Field Mapping Reference

### Common field renames:
- `nombres` + `apellidos` → `nombre` (Persona)
- `documento` → `curp` (Persona)
- `codigo` → `descripcion` (Hangar)
- `matricula` → removed (Nave)
- `fechaSalida` → `fecha_salida` (Vuelo)
- `fechaRegistro` → `fecha` (Reporte)
- `nombre` → `rol` (Rol)

### Removed fields:
- Empresa: `pais`
- Hangar: `codigo`, `ubicacion`, `empresa`
- Taller: `nombre`, `especialidad`
- Modelo: `nombre`, `fabricante`, `capacidad`
- Persona: `nombres`, `apellidos`, `documento`, `rol`
- Piloto: `licencia`, `experiencia`
- Nave: `matricula`
- Vuelo: `codigo`, `tripulacion`
- Tripulacion: `nombre`, `integrantes`
- Reporte: `titulo`

### Added fields:
- Empresa: `contacto`, `ubicacion`, `rfc`
- Encargado: `nombre`
- Hangar: `descripcion`, `area`, `num`
- Taller: `encargado`
- Modelo: `peso`, `pais_fabricacion`
- Persona: `nombre`, `curp`, `edad`, `celular`, `hrs_vuelo`
- Rol: `persona`, `rol`
- Piloto: `persona`, `modelo`, `hrs_voladas`
- Nave: `peso`, `capacidad`
- Vuelo: `origen`, `fecha_llegada`, `pasajeros`, `distancia`
- Tripulacion: `vuelo`, `persona`, `rol`
- Reporte: `encargado`, `diagnostico`, `acciones_realizadas`, `costo`
