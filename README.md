# Hangar CRUD (Spring Boot + JavaFX)

This project is a desktop management panel for a hangar operation. It uses **Spring Boot** with **Spring Data JPA** to persist entities in MySQL and **JavaFX** to present a CRUD interface that covers aircraft, crews, roles, maintenance workshops, and scheduled flights.

## Features
- **Seeded demo data**: `DataSeeder` populates roles, people, pilots, companies, hangars, workshops, aircraft, crews, flights, and maintenance reports on first launch to provide sample records.
- **CRUD operations**: Each domain (companies, hangars, aircraft, pilots, crews, flights, roles, people, maintenance reports, etc.) has a JavaFX view wired to Spring-managed controllers and services that perform create, read, update, and delete actions through Spring Data repositories.
- **JavaFX + Spring integration**: `HangarFxApp` bootstraps the Spring context, then loads FXML scenes with controllers supplied by Spring, sharing the same services and database connections.
- **MySQL persistence**: Uses Spring Data JPA with MySQL; schema is auto-generated/updated via `spring.jpa.hibernate.ddl-auto=update`.

## Project structure
- `src/main/java/com/example/hangar/HangarApplication.java` – Standard Spring Boot entrypoint.
- `src/main/java/com/example/hangar/HangarFxApp.java` – JavaFX launcher that starts Spring and loads `main-view.fxml`.
- `src/main/java/com/example/hangar/config/` – Configuration beans, including `DataSeeder` for initial data and JPA configuration.
- `src/main/java/com/example/hangar/model/` – JPA entities (Hangar, Empresa, Nave, Taller, Vuelo, etc.).
- `src/main/java/com/example/hangar/repository/` – Spring Data repositories for each entity.
- `src/main/java/com/example/hangar/service/` – Service interfaces/implementations used by the UI controllers.
- `src/main/java/com/example/hangar/ui/controller/` – JavaFX controllers for each view loaded in the main panel.
- `src/main/resources/fxml/` – FXML layouts for each CRUD screen plus the main dashboard.
- `src/main/resources/application.properties` – Database connection and JPA settings.

## Prerequisites
- Java 21
- Maven 3.9+
- A running MySQL instance with a database named `hangar` and credentials matching `application.properties` (defaults: user `root`, password `root`).

## Running the application
1. Clone the repository and move into the project directory.
2. Configure database access in `src/main/resources/application.properties` if needed.
3. Ensure the target database exists: `CREATE DATABASE hangar;` (tables are created automatically by Hibernate on startup).
4. Build and run the JavaFX application:
   ```bash
   ./mvnw clean package
   ./mvnw javafx:run
   ```
   The JavaFX window titled **"Hangar - Panel Principal"** will open.

## Usage
- The main dashboard provides buttons that load each CRUD view (companies, hangars, pilots, workshops, crews, flights, aircraft, roles, people, reports, models, etc.).
- Each view allows adding new records, editing selected rows, or deleting them via the Spring-backed services.
- On first launch the seeded demo data appears automatically, giving you example rows to explore before adding your own.

## Notes
- SQL logging is enabled by default (`logging.level.org.hibernate.SQL=debug`) to help observe generated queries.
- Adjust `spring.jpa.hibernate.ddl-auto` if you prefer a different schema management strategy.
- Feel free to contribute through a pull request.
