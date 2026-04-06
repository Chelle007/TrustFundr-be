# trustfundr-be

Backend service for TrustFundr built with **Java 21**, **Spring Boot**, **Spring Security**, **Spring Data JPA**, and **PostgreSQL**.

## Requirements

- **Java**: 21
- **Database**: PostgreSQL (local or hosted)

## Setup

This project loads environment variables from a local `.env` file (see `TrustfundrBeApplication.java`).

1. Create a `.env` file in the project root (it is gitignored).
2. Add the required variables:

```env
# CORS
ALLOWED_ORIGIN=http://localhost:5173

# Database
DB_URL=jdbc:postgresql://localhost:5432/trustfundr
DB_USERNAME=postgres
DB_PASSWORD=your_password
# e.g. create-drop | update | validate
DB_UPDATE=update

# Swagger / OpenAPI
API_DOCS=/v1/api-docs
API_DOCS_HTML=/swagger-ui.html
DEV_URL=http://localhost:8080
STG_URL=http://localhost:8080
PROD_URL=http://localhost:8080

# Optional SQL logging
JPA_SHOW_SQL=false
JPA_FORMAT_SQL=false
```

## Run locally

```bash
./mvnw spring-boot:run
```

The app defaults to port **8080** (unless you override `server.port`).

## Tests

```bash
./mvnw test
```

## Build

```bash
./mvnw clean package
```

The jar will be under `target/`.

## API docs (Swagger)

With the default `.env` values above:

- **OpenAPI JSON**: `http://localhost:8080/v1/api-docs`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

