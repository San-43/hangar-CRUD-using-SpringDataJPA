# Hangar CRUD Application - Spring Data JPA

## Overview
This is a Spring Boot application for managing hangar operations using Spring Data JPA and JavaFX for the user interface.

## Recent Changes - Database Schema Migration

The application has been updated to work with a new database schema. The data access layer (entities, repositories, and services) has been successfully migrated.

### ✅ Completed
- Entity models updated to match new schema
- Repository interfaces updated
- Service layer updated
- ID types changed from Long to Integer across all entities
- Build configuration updated (Java 17, JavaFX 17)

### ⚠️ In Progress
- **JavaFX UI Controllers**: The UI controllers require updates to work with the new entity structure. See [SCHEMA_MIGRATION.md](SCHEMA_MIGRATION.md) for detailed migration guide.
- **DataSeeder**: Sample data seeding is currently disabled and needs to be rewritten for the new schema.

## Database Configuration

The application connects to a MySQL database. Update the following properties in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hangar?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
```

### Database Schema

The application now uses the following schema:

- **empresa**: Stores company information (id_empresa, nombre, contacto, ubicacion, rfc)
- **encargado**: Person in charge (id_encargado, nombre)
- **hangar**: Hangar facilities (id_hangar, descripcion, capacidad, area, num)
- **taller**: Workshop/garage (id_taller, id_hangar, id_encargado)
- **modelo**: Aircraft model (id_modelo, peso, pais_fabricacion)
- **persona**: Person (id_persona, nombre, curp, edad, celular, hrs_vuelo)
- **rol**: Role (id_rol, id_persona, rol)
- **piloto**: Pilot (id_piloto, id_persona, id_modelo, hrs_voladas)
- **naves**: Aircraft/vessels (id_nave, id_empresa, id_hangar, id_modelo, peso, capacidad, estado)
- **vuelo**: Flight (id_vuelo, id_nave, origen, destino, fecha_salida, fecha_llegada, pasajeros, distancia)
- **tripulacion**: Crew (id_tripulacion, id_vuelo, id_persona, id_rol)
- **reporte**: Report (id_reporte, id_nave, id_taller, id_encargado, diagnostico, acciones_realizadas, fecha, costo)

See [SCHEMA_MIGRATION.md](SCHEMA_MIGRATION.md) for complete schema migration details.

## Build and Run

### Prerequisites
- Java 17
- Maven
- MySQL Server

### Building
```bash
./mvnw clean compile
```

Note: Currently, the JavaFX UI controllers have compilation errors due to the schema migration. The data layer compiles successfully.

### Running
```bash
./mvnw spring-boot:run
```

## Project Structure

```
src/main/java/com/example/hangar/
├── config/          # Spring configuration
├── model/           # JPA entity models (✅ Updated)
├── repository/      # Spring Data repositories (✅ Updated)
├── service/         # Business logic layer (✅ Updated)
├── ui/controller/   # JavaFX controllers (⚠️ Needs update)
└── util/            # Utility classes
```

## Migration Status

For developers working on completing the migration:

1. **Data Layer** (✅ Complete): All JPA entities, repositories, and services have been updated
2. **UI Layer** (⚠️ Pending): JavaFX controllers need to be updated to match new entity structure
3. **Data Seeding** (⚠️ Pending): DataSeeder needs to be rewritten

Refer to [SCHEMA_MIGRATION.md](SCHEMA_MIGRATION.md) for the complete migration guide and field mapping reference.

## Technologies Used

- Spring Boot 3.3.5
- Spring Data JPA
- MySQL
- JavaFX 17
- Maven

## License

This project is for educational purposes.
