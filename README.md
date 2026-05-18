# trustfundr-be

Backend API for [TrustFundr](../TrustFundr/) — **Java 21**, **Spring Boot**, **Spring Security (JWT)**, **Spring Data JPA**, and **PostgreSQL**.

## Requirements

- **Java** 21
- **PostgreSQL** (local install or hosted, e.g. Supabase)

## Setup

Environment variables are loaded from a `.env` file in this directory (`@PropertySource` on `TrustfundrBeApplication`). **Run Maven commands from this folder** so `user.dir` resolves to the correct path.

1. Copy the example file:

   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with your database credentials and a JWT secret (at least 32 bytes for HS256).

### Local PostgreSQL example

```env
ALLOWED_ORIGIN=http://localhost:3000

DB_URL=jdbc:postgresql://localhost:5432/trustfundr
DB_USERNAME=postgres
DB_PASSWORD=your_password
DB_UPDATE=update
```

### Hosted PostgreSQL (e.g. Supabase)

Use the pooler connection string from your provider and include SSL if required:

```env
DB_URL=jdbc:postgresql://your-host:5432/postgres?sslmode=require
DB_USERNAME=your_user
DB_PASSWORD=your_password
DB_UPDATE=update
```

See [`.env.example`](.env.example) for the full list of variables.

## Run locally

```bash
./mvnw spring-boot:run
```

The API listens on **http://localhost:8080** by default.

On startup, `DataInitializer` runs seeders (profiles, accounts, categories, activities, donations, favourites). Default logins for development:

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Donee | `donee` | `donee123` |
| Fundraiser | `fundraiser` | `fundraiser123` |
| Platform manager | `platform` | `platform123` |

Additional faker data is generated up to the configured targets (e.g. 100 accounts).

## Full stack with the frontend

1. Start this backend (`./mvnw spring-boot:run`).
2. In [`../TrustFundr`](../TrustFundr/), run `npm install` and `npm run dev`.
3. Open **http://localhost:3000** and log in with a seeded account.

Set `ALLOWED_ORIGIN=http://localhost:3000` so the Next.js app can call the API. The default in `application.properties` also allows `http://127.0.0.1:3000`.

## Tests

```bash
./mvnw test
```

## Build

```bash
./mvnw clean package
```

The JAR is written to `target/`.

## API docs (Swagger)

With default `.env` values:

- **OpenAPI JSON**: http://localhost:8080/v1/api-docs
- **Swagger UI**: http://localhost:8080/swagger-ui.html
