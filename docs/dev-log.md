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

### 本次新增：用户注册模块第一版

新增后端依赖：

- MyBatis-Plus：替代旧项目里手写 JDBC/DAO 的方式，用 Mapper 操作数据库。
- MySQL Driver：让 Java 程序能连接 MySQL。
- Spring Security Crypto：只使用其中的 BCrypt 密码加密工具，暂时还没有启用完整权限控制。

新增代码：

- `user/User.java`：用户表对应的 Java 实体。
- `user/UserMapper.java`：操作 `users` 表的 Mapper。
- `user/RegisterRequest.java`：注册接口接收的请求参数。
- `user/UserResponse.java`：注册成功后返回给前端的数据。
- `user/UserService.java`：注册业务逻辑，包括用户名查重和密码加密。
- `user/UserController.java`：注册接口入口。
- `schema.sql`：第一张表 `users` 的建表 SQL。

新增接口：

```text
POST /api/users/register
```

请求示例：

```json
{
  "username": "testuser",
  "password": "123456",
  "phone": "13800000000",
  "email": "test@example.com"
}
```

这一版还没有 JWT，也没有登录接口。现在只是先把“注册用户并保存到数据库”这条链路搭出来。
