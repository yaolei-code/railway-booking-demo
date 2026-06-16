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

### 本次验证：注册接口真实写入数据库

已经确认：

- 本机 MySQL 服务 `MySQL80` 正在运行。
- 正确的本地数据库账号是 `root`，密码通过环境变量 `DB_PASSWORD` 提供。
- 已创建数据库 `railway_booking`。
- 已创建数据表 `users`。
- `POST /api/users/register` 已经成功注册测试用户。

为了方便在 IDEA 里测试接口，新增：

- `backend/api-tests.http`

这个文件里包含：

- `GET /api/health`
- `POST /api/users/register`

注意：浏览器地址栏默认发的是 GET 请求，所以不能直接用地址栏测试注册接口。注册接口必须用 POST，并且带 JSON 请求体。

### 本次新增：用户登录接口第一版

新增代码：

- `user/LoginRequest.java`：登录接口接收的请求参数。
- `user/LoginResponse.java`：登录成功后返回给前端的数据。

更新代码：

- `UserService`：新增 `login` 方法。
- `UserController`：新增 `POST /api/users/login` 接口。
- `api-tests.http`：新增登录接口测试请求。

登录流程：

```text
前端提交 username 和 password
  -> UserController 接收请求
  -> UserService 根据 username 查询 users 表
  -> 使用 BCrypt 校验密码是否匹配
  -> 校验用户状态是否 ENABLED
  -> 返回登录成功的用户信息
```

注意：这一版登录成功后还没有返回 JWT。现在只是先验证“用户名 + 密码是否正确”。下一步会加入 JWT，让后端能够识别“这个请求是谁发来的”。

### 本次新增：JWT 登录通行证第一版

新增依赖：

- JJWT：用于生成和解析 JWT token。

新增代码：

- `common/UnauthorizedException.java`：表示用户没有登录或 token 无效。
- `user/JwtService.java`：负责生成 token、解析 token。

更新代码：

- `LoginResponse`：登录成功后返回 token。
- `UserService`：登录成功后生成 token，并新增当前用户查询逻辑。
- `UserController`：新增 `GET /api/users/me`。
- `GlobalExceptionHandler`：把未登录或 token 无效统一返回 401。
- `api-tests.http`：新增当前用户接口测试。

现在用户模块的流程是：

```text
注册
  -> 保存用户

登录
  -> 校验用户名和密码
  -> 返回 JWT token

查询当前用户
  -> 请求头带 Authorization: Bearer token
  -> 后端解析 token
  -> 返回当前登录用户信息
```

可以先把 JWT 理解成“登录成功后发给用户的一张临时通行证”。后续查订单、下单等需要登录的接口，都会要求用户带上这张通行证。

### 本次新增：基础业务日志

新增日志位置：

- `UserService`：记录用户注册成功、登录成功、登录失败原因。
- `GlobalExceptionHandler`：记录 400、401、405 和未处理的 500 异常。

日志原则：

- 可以记录用户 ID、用户名、业务结果。
- 不记录明文密码。
- 不记录 JWT token。
- 500 异常需要记录完整异常堆栈，方便排查代码问题。

这一步让项目更接近企业后端。真实项目上线后，排查问题通常不是靠猜，而是看日志。

### 阶段交接文档

用户模块阶段已经整理成独立总结：

- `docs/stage-01-user-module-summary.md`

如果切换到新的 Codex 窗口，可以让新窗口先阅读这份文档，再继续车站模块。
