# Railway Booking System Project Plan

## 1. Project Goal

Build a resume-ready railway ticket booking system with a Java backend and a Vue frontend. The project should demonstrate practical backend engineering ability, including authentication, relational database modeling, order workflows, inventory deduction, concurrency control, API design, and deployable project structure.

## 2. Resume Positioning

Project name: Railway Booking System

Target role:

- Java Backend Developer
- Java Full-Stack Developer

Core highlights:

- Spring Boot backend with layered architecture
- MySQL relational schema for train booking business
- JWT authentication and role-based access
- Ticket query and order workflow
- Redis inventory cache and anti-oversell design
- Scheduled task for unpaid order timeout cancellation
- Docker Compose local deployment
- Clear README, API documentation, and database scripts

## 3. Recommended Tech Stack

Backend:

- Java 17
- Spring Boot 3
- Spring Security
- JWT
- MyBatis-Plus
- Knife4j / OpenAPI

Storage:

- MySQL
- Redis

Frontend:

- Vue 3
- Vite
- Element Plus
- Axios
- Pinia

DevOps:

- Maven
- Docker Compose
- Git

## 4. Business Modules

### 4.1 User Module

Features:

- User registration
- User login
- JWT token issuance
- Current user profile
- Basic role distinction: user and admin

Main tables:

- users
- user_roles

### 4.2 Station Module

Features:

- Station list
- Station creation, update, deletion for admin
- Station search by city or station name

Main table:

- stations

### 4.3 Train Module

Features:

- Train list
- Train details
- Train schedule management
- Stop station management
- Seat type and price configuration

Main tables:

- trains
- train_stations
- train_carriages
- train_seats
- train_seat_prices

### 4.4 Ticket Query Module

Features:

- Query by departure station, arrival station, and travel date
- Show train number, departure time, arrival time, duration, price, and remaining tickets
- Filter by seat type

Main tables:

- train_daily_schedules
- ticket_inventory

### 4.5 Order Module

Features:

- Create ticket order
- Lock or deduct inventory
- Simulated payment
- Cancel unpaid order
- View order list and order detail

Order statuses:

- PENDING_PAYMENT
- PAID
- CANCELLED
- REFUNDED

Main tables:

- ticket_orders
- ticket_order_items

### 4.6 Payment Module

Features:

- Simulated payment endpoint
- Payment status update
- Payment record storage

Main table:

- payments

### 4.7 Admin Module

Features:

- Manage stations
- Manage trains and schedules
- Manage ticket inventory
- View orders

## 5. Database Draft

Initial table list:

- users
- roles
- user_roles
- stations
- trains
- train_stations
- train_carriages
- train_seats
- train_daily_schedules
- ticket_inventory
- ticket_orders
- ticket_order_items
- payments

Detailed SQL design will be placed in `docs/database-design.md`.

## 6. API Draft

Public APIs:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/stations`
- `GET /api/tickets/search`

User APIs:

- `GET /api/users/me`
- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `POST /api/orders/{id}/pay`
- `POST /api/orders/{id}/cancel`

Admin APIs:

- `POST /api/admin/stations`
- `PUT /api/admin/stations/{id}`
- `DELETE /api/admin/stations/{id}`
- `POST /api/admin/trains`
- `PUT /api/admin/trains/{id}`
- `POST /api/admin/schedules`
- `POST /api/admin/inventory`

Detailed API design will be placed in `docs/api-design.md`.

## 7. Development Milestones

### Milestone 1: Project Foundation

- Create Git repository: done
- Create project planning documents: done
- Generate Spring Boot backend structure: done
- Add basic Maven dependencies: done
- Add standard response format and global exception handling: done

### Milestone 2: User Authentication

- Implement user registration: done
- Implement user login: done
- Add password encryption: done
- Add JWT token issuance: done
- Add current-user endpoint: done

### Milestone 3: Station and Train Data

- Design station and train tables
- Implement station CRUD: station CRUD done
- Implement train and stop station management: first version done
- Add initial seed data

### Milestone 4: Ticket Search

- Implement travel date schedule model: first version done
- Implement remaining ticket inventory model: first version done
- Implement ticket search API: first version done
- Add basic query tests

### Milestone 5: Order Workflow

- Implement order creation: first version done
- Implement inventory deduction: first version locks one ticket
- Implement order payment simulation: first version done
- Implement order cancellation: first version done
- Add timeout cancellation scheduled task: first version done

### Milestone 6: Concurrency and Redis

- Cache ticket inventory in Redis
- Add atomic inventory deduction with Redis Lua or database optimistic lock: database atomic update first version done
- Add oversell prevention test

### Milestone 7: Frontend

- Create Vue 3 frontend
- Build login and register pages
- Build ticket search page
- Build order list and order detail pages
- Build simple admin pages

### Milestone 8: Deployment and Resume Polish

- Add Docker Compose for MySQL, Redis, backend, and frontend
- Add API documentation
- Improve README
- Add screenshots
- Write resume bullet points

## 8. Git Workflow

Use one commit per small milestone or feature.

Examples:

```bash
git status
git add .
git commit -m "add project planning docs"
git commit -m "create spring boot backend"
git commit -m "add user login"
git commit -m "add ticket search api"
```

Before using AI tools to make large changes, commit the current stable state first.

## 9. AI Collaboration Notes

When switching between Codex, Cursor, and Claude Code, ask the tool to read these files first:

- `README.md`
- `docs/project-plan.md`
- `docs/database-design.md`
- `docs/api-design.md`
- `docs/dev-log.md`

Keep important decisions in docs so the project context is not locked inside one chat history.

## 10. Next Step

Next recommended task:

Create the Spring Boot backend skeleton and define the first version of the database design.
