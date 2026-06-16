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

## 2026-06-16

### 今天做了什么

进入第二阶段前，先补充数据库设计文档：

- `docs/database-design.md`

这一步不是直接写登录代码，而是先把系统里要保存的数据想清楚。铁路订票系统后面会有用户、角色、车站、车次、余票、订单、支付等模块，这些模块最终都需要落到数据库表里。

### 今天要理解什么

后端项目可以先按这条线理解：

```text
浏览器请求
  -> Controller 接请求
  -> Service 处理业务规则
  -> Mapper/DAO 查数据库
  -> MySQL 保存数据
```

你之前做过的学生选课系统里已经有类似结构：

- `Controller`：接收页面请求，例如登录、查看课程。
- `DAO`：写 SQL，连接数据库。
- `Entity`：表示数据库里的一行数据，例如 `Student`、`Course`。

现在这个铁路项目会用更规范的写法重新做一遍。

### 下一步

开始做用户模块的最小版本：

1. 添加用户相关依赖和配置。
2. 创建 `User` 实体。
3. 创建注册接口。
4. 创建登录接口。
5. 后续再加入 JWT 和权限控制。
