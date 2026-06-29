# Railway Booking Demo

A Java full-stack resume project for a railway ticket booking system.

## Positioning

This project is designed for a Java backend / full-stack developer resume. It focuses on realistic booking workflows, database design, authentication, ticket inventory, order status transitions, and concurrency control.

## Planned Tech Stack

- Backend: Java 17, Spring Boot 3
- Database: MySQL
- Cache and concurrency: Redis
- ORM: MyBatis-Plus
- Auth: Spring Security, JWT
- API docs: Knife4j / OpenAPI
- Frontend: Vue 3, Vite, Element Plus
- Deployment: Docker Compose

## Planned Modules

- User registration and login
- Station and train management
- Train schedule and remaining ticket query
- Ticket order creation
- Simulated payment
- Order cancellation and timeout handling
- Admin management APIs
- Redis-based inventory protection

## Development Status

Current stage: backend core workflow completed in first version, including user, station, train, ticket search, order, payment, cancellation, unpaid order timeout handling, and database-level anti-oversell. Spring Security protects `/api/admin/**` with the `ADMIN` role. The Vue frontend has been split into route-based user and admin pages. Knife4j / OpenAPI documentation is available for backend APIs.

See [docs/project-plan.md](docs/project-plan.md) for the detailed development plan.

## Quick Start (Docker Compose)

One command to start MySQL, backend, and frontend:

```bash
docker compose up -d
```

This will:
- Start MySQL 8.0, auto-create tables and load demo data
- Build and start the Spring Boot backend
- Build and start the Vue frontend (served by Nginx)

After startup:

| Service | URL |
|---------|-----|
| Frontend | http://localhost:5173 |
| API health | http://localhost:8080/api/health |
| Knife4j docs | http://localhost:8080/doc.html |
| Swagger UI | http://localhost:8080/swagger-ui.html |

Stop everything:

```bash
docker compose down
```

To rebuild after code changes:

```bash
docker compose up -d --build
```

> **Prerequisites:** Docker and Docker Compose. [Get Docker](https://docs.docker.com/get-docker/).

## Local Backend

Backend module path:

```bash
backend
```

Run from the backend directory:

```bash
mvn spring-boot:run
```

Health check:

```text
GET http://localhost:8080/api/health
```

API documentation:

```text
Knife4j UI: http://localhost:8080/doc.html
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
```

Load demo data after creating the schema:

```bash
mysql --default-character-set=utf8mb4 -uroot -p050607 railway_booking < backend/src/main/resources/demo-data.sql
```

Demo ticket search:

```text
GET http://localhost:8080/api/tickets/search?departureStationId=1&arrivalStationId=2&travelDate=2026-06-20
```

The demo script currently seeds 12 stations, 5 trains, 5 travel dates, and multiple seat inventories.

Demo admin account:

```text
username: admin
password: admin123
```

See [docs/api-design.md](docs/api-design.md) for the current backend API list.

## Local Frontend

Frontend module path:

```bash
frontend
```

Install dependencies:

```bash
npm install
```

Run from the frontend directory:

```bash
npm run dev
```

Default frontend URL:

```text
http://localhost:5173
```

Frontend routes:

```text
/tickets
/orders
/account
/admin
```
