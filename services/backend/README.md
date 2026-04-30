# Ticketeer — Backend API

Spring Boot 3 REST API for the Ticketeer railway ticketing system.

## Architecture

Modular monolith following **Clean Architecture** and **Domain-Driven Design**:

```
src/
  identity/     — Authentication & users (JWT, BCrypt)
  network/      — Stations & trip search
  ticketing/    — Ticket issuance, QR code, PDF generation
  control/      — Ticket validation for agents
  shared/       — Cross-cutting: exceptions, DateRange, DomainClock
  security/     — JWT filter chain, Spring Security config
  bootstrap/    — Seed data on startup
  config/       — Bean wiring (AppConfig, OpenApiConfig)
```

## Running locally (H2 — no database needed)

```bash
cd services/backend
mvn spring-boot:run
```

The app starts on **http://localhost:8080** with an in-memory H2 database.

## Running with PostgreSQL

```bash
# Start the database
docker compose up postgres -d

# Run with the postgres profile
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
```

## Running everything with Docker

```bash
docker compose up --build
```

## API documentation

Swagger UI: **http://localhost:8080/swagger-ui.html**

## Demo accounts

| Role    | Email                    | Password    |
|---------|--------------------------|-------------|
| Client  | mia@ticketeer.com        | user123     |
| Client  | lola@ticketeer.com       | user123     |
| Agent   | monir@ticketeer.com      | control123  |
| Admin   | ouail@ticketeer.com      | admin123    |

## Key endpoints

| Method | Path                        | Role            | Description           |
|--------|-----------------------------|-----------------|-----------------------|
| POST   | /auth/login                 | Public          | Get JWT token         |
| GET    | /trips/search               | Public          | Search trips by date  |
| POST   | /tickets                    | CUSTOMER, ADMIN | Purchase a ticket     |
| GET    | /tickets/me                 | CUSTOMER, ADMIN | List my tickets       |
| GET    | /tickets/{id}/qr            | CUSTOMER, ADMIN | Download QR code      |
| GET    | /tickets/{id}/pdf           | CUSTOMER, ADMIN | Download PDF ticket   |
| POST   | /control/validate           | AGENT, ADMIN    | Validate a ticket     |

## Tech stack

- Java 17 + Spring Boot 3.3
- PostgreSQL / H2 (dev)
- Flyway (schema migrations)
- JJWT 0.12 (JWT standard)
- ZXing (QR code generation)
- OpenPDF (PDF generation)
- Springdoc OpenAPI (Swagger UI)
