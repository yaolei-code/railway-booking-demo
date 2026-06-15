# Development Log

## 2026-06-15

### What We Did

Initialized the project repository and created the first planning documents:

- `README.md`
- `docs/project-plan.md`

Created the first Spring Boot backend skeleton:

- `backend/pom.xml`
- `backend/src/main/java/com/example/railway/RailwayBookingApplication.java`
- `backend/src/main/java/com/example/railway/common/ApiResponse.java`
- `backend/src/main/java/com/example/railway/common/GlobalExceptionHandler.java`
- `backend/src/main/java/com/example/railway/health/HealthController.java`
- `backend/src/main/resources/application.yml`
- `backend/src/test/java/com/example/railway/RailwayBookingApplicationTests.java`

### What To Learn

`pom.xml` is the Maven project file. It defines the project name, Java version, dependencies, and build plugins.

`RailwayBookingApplication.java` is the Spring Boot startup class. Running this class starts the backend server.

`application.yml` stores backend configuration, such as the server port and application name.

`HealthController.java` defines the first API endpoint:

```text
GET /api/health
```

This endpoint is used to check whether the backend service is running.

`ApiResponse.java` defines a unified response format. In real projects, backend APIs usually return data in a consistent structure.

Example:

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

`GlobalExceptionHandler.java` catches backend exceptions and returns a consistent error response.

### Git Reminder

After checking the files in Cursor, commit this stage:

```bash
git status
git add .
git commit -m "create spring boot backend skeleton"
```

### Next Step

Run the backend locally and call the health check API:

```bash
cd backend
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080/api/health
```
