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

Current stage: backend core workflow completed in first version, including user, station, train, ticket search, order, payment, cancellation, unpaid order timeout handling, and database-level anti-oversell.

See [docs/project-plan.md](docs/project-plan.md) for the detailed development plan.

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

Load demo data after creating the schema:

```bash
mysql -uroot -p050607 railway_booking < backend/src/main/resources/demo-data.sql
```

Demo ticket search:

```text
GET http://localhost:8080/api/tickets/search?departureStationId=1&arrivalStationId=2&travelDate=2026-06-20
```

The demo script currently seeds 12 stations, 5 trains, 5 travel dates, and multiple seat inventories.

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
