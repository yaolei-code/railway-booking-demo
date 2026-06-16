# 阶段总结：用户模块

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前项目定位

项目名称：Railway Booking Demo

目标：做一个适合 Java 后端 / 全栈简历展示的铁路订票系统。

当前阶段已经完成后端用户模块第一版。系统已经不只是 Spring Boot 空壳，而是具备了真实的用户注册、登录、身份识别和基础日志能力。

## 2. 当前技术栈

- Java 17
- Spring Boot 3.3.5
- Maven
- MySQL 8
- MyBatis-Plus
- BCrypt 密码加密
- JWT 登录 token
- Spring Boot 默认日志体系 SLF4J + Logback

## 3. 当前已完成内容

### 3.1 后端基础

- Spring Boot 项目骨架
- Maven 依赖管理
- 统一接口返回格式 `ApiResponse`
- 全局异常处理 `GlobalExceptionHandler`
- 健康检查接口

接口：

```text
GET /api/health
```

### 3.2 数据库基础

本地 MySQL 已经创建数据库：

```text
railway_booking
```

已创建数据表：

```text
users
```

建表 SQL 在：

```text
backend/src/main/resources/schema.sql
```

数据库密码不应该写死在代码里。本机已设置 Windows 用户环境变量：

```text
DB_USERNAME=root
DB_PASSWORD=050607
```

如果 IDEA 读取不到环境变量，重启 IDEA。

### 3.3 用户注册

接口：

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

功能：

- 校验用户名、密码不能为空
- 检查用户名是否重复
- 使用 BCrypt 加密密码
- 写入 `users` 表
- 返回用户基本信息

注意：数据库不保存明文密码，只保存 `password_hash`。

### 3.4 用户登录

接口：

```text
POST /api/users/login
```

请求示例：

```json
{
  "username": "testuser",
  "password": "123456"
}
```

功能：

- 根据用户名查询用户
- 使用 BCrypt 校验密码
- 检查用户状态是否为 `ENABLED`
- 登录成功后生成 JWT token
- 返回用户基本信息和 token

### 3.5 当前用户识别

接口：

```text
GET /api/users/me
```

请求头示例：

```text
Authorization: Bearer 登录接口返回的token
```

功能：

- 从请求头读取 token
- 校验 token 是否有效
- 解析 token 中的用户 ID
- 查询并返回当前用户信息

可以把 JWT 理解成登录成功后后端发给用户的一张临时通行证。

### 3.6 基础日志

已添加基础业务日志：

- 用户注册成功
- 用户注册失败：用户名重复
- 用户登录成功
- 用户登录失败：用户不存在 / 密码错误 / 账号禁用
- token 无效或缺失
- 未处理的 500 异常

日志原则：

- 可以记录用户 ID、用户名和业务结果
- 不记录明文密码
- 不记录 JWT token
- 不记录数据库密码

## 4. 当前核心代码位置

启动类：

```text
backend/src/main/java/com/example/railway/RailwayBookingApplication.java
```

通用模块：

```text
backend/src/main/java/com/example/railway/common/ApiResponse.java
backend/src/main/java/com/example/railway/common/GlobalExceptionHandler.java
backend/src/main/java/com/example/railway/common/UnauthorizedException.java
```

用户模块：

```text
backend/src/main/java/com/example/railway/user/User.java
backend/src/main/java/com/example/railway/user/UserMapper.java
backend/src/main/java/com/example/railway/user/UserService.java
backend/src/main/java/com/example/railway/user/UserController.java
backend/src/main/java/com/example/railway/user/RegisterRequest.java
backend/src/main/java/com/example/railway/user/UserResponse.java
backend/src/main/java/com/example/railway/user/LoginRequest.java
backend/src/main/java/com/example/railway/user/LoginResponse.java
backend/src/main/java/com/example/railway/user/JwtService.java
```

接口测试：

```text
backend/api-tests.http
```

## 5. 当前可以怎么测试

在 IDEA 中打开：

```text
backend/api-tests.http
```

依次测试：

1. `GET /api/health`
2. `POST /api/users/register`
3. `POST /api/users/login`
4. 复制登录返回的 token
5. 粘贴到 `GET /api/users/me` 的 `Authorization` 请求头里
6. 测试当前用户接口

也可以运行：

```bash
cd backend
mvn test
```

当前测试结果应为：

```text
BUILD SUCCESS
```

## 6. 当前学习理解

可以用这一句话理解后端项目：

```text
后端接收请求，处理业务规则，操作数据库，然后返回结果。
```

当前用户模块的完整链路：

```text
浏览器 / 接口测试工具
  -> UserController 接请求
  -> UserService 做业务判断
  -> UserMapper 操作 MySQL
  -> users 表保存或查询数据
  -> ApiResponse 返回统一 JSON
```

## 7. 下一阶段建议

建议切换到新窗口后开发：车站模块。

原因：

铁路订票系统要先有基础数据，才能查票和下单。

推荐下一阶段目标：

```text
车站模块
```

计划完成：

1. 创建 `stations` 表 SQL
2. 创建 `Station` 实体
3. 创建 `StationMapper`
4. 创建 `StationService`
5. 创建 `StationController`
6. 实现车站列表查询
7. 实现新增车站
8. 实现修改车站
9. 实现删除车站
10. 在 `api-tests.http` 中补充车站接口测试

建议优先做：

```text
GET /api/stations
POST /api/admin/stations
```

后面再补：

```text
PUT /api/admin/stations/{id}
DELETE /api/admin/stations/{id}
```

## 8. 新窗口开场提示词

如果切换到新 Codex 窗口，可以直接发送：

```text
请继续我的 railway-booking-demo 项目。先阅读 README.md、docs/project-plan.md、docs/database-design.md、docs/dev-log.md 和 docs/stage-01-user-module-summary.md，然后从车站模块开始开发。请边做边讲，适合初学者理解。
```

