# 铁路订票系统 Railway Booking Demo

这是一个面向 Java 后端 / 全栈开发岗位的铁路订票系统项目。项目覆盖用户登录、车票查询、订单创建、模拟支付、订单取消、库存防超卖、管理员后台、接口文档和 Docker Compose 部署等完整链路，适合作为简历项目和面试讲解项目。

## 项目亮点

- 基于 Spring Boot 3 构建分层后端，包含 Controller、Service、Mapper、Entity 等常见企业项目结构。
- 使用 Spring Security + JWT 实现登录认证和角色权限控制，普通用户为 `USER`，管理员为 `ADMIN`。
- 支持车站、车次、经停车站、每日开行计划、车票库存、订单和支付等铁路订票核心业务模型。
- 下单时使用 Redis Lua 原子锁票，并使用 MySQL 原子 `UPDATE ... WHERE available_count > 0` 作为最终防超卖兜底。
- 支持未支付订单定时超时取消，自动释放锁定库存。
- Vue 3 前端已拆分为查票、订单、账号、管理台等页面路由。
- 接入 Knife4j / OpenAPI，可在浏览器中查看和调试后端接口。
- 提供 Docker Compose，可一键启动 MySQL、Redis、后端和前端。

## 技术栈

后端：

- Java 17
- Spring Boot 3
- Spring Security
- JWT
- MyBatis-Plus
- MySQL
- Redis
- Knife4j / OpenAPI

前端：

- Vue 3
- Vite
- Element Plus
- Vue Router

部署：

- Docker
- Docker Compose
- Nginx

## 已实现功能

- 用户注册、登录、JWT 签发和当前用户查询
- 车站列表和管理员车站管理
- 车次列表、车次管理和经停车站管理
- 按出发站、到达站、日期和座位类型查询余票
- 创建订单并锁定库存
- 模拟支付并确认库存
- 取消未支付订单并释放库存
- 定时扫描并取消超时未支付订单
- 管理员接口权限控制
- 前端路由守卫和管理员页面
- Knife4j / Swagger 接口文档
- Docker Compose 本地部署

## 项目结构

```text
railway-booking-demo
├── backend    Spring Boot 后端
├── frontend   Vue 3 前端
├── docs       项目设计文档和阶段总结
└── docker-compose.yml
```

## 快速启动（Docker Compose）

项目根目录执行：

```bash
docker compose up -d
```

启动后访问：

| 服务 | 地址 |
| --- | --- |
| 前端页面 | http://localhost:5173 |
| 后端健康检查 | http://localhost:8080/api/health |
| Knife4j 接口文档 | http://localhost:8080/doc.html |
| Swagger UI | http://localhost:8080/swagger-ui.html |

停止服务：

```bash
docker compose down
```

代码变更后重新构建：

```bash
docker compose up -d --build
```

## 本地后端启动

进入后端目录：

```bash
cd backend
mvn spring-boot:run
```

健康检查：

```text
GET http://localhost:8080/api/health
```

接口文档：

```text
Knife4j UI: http://localhost:8080/doc.html
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
```

## 本地前端启动

进入前端目录：

```bash
cd frontend
npm install
npm run dev
```

默认访问地址：

```text
http://localhost:5173
```

前端路由：

```text
/tickets   查票下单
/orders    我的订单
/account   登录注册
/admin     管理台
```

## 演示数据

Docker Compose 会自动执行建表和演示数据初始化脚本。手动导入时可执行：

```bash
mysql --default-character-set=utf8mb4 -uroot -p050607 railway_booking < backend/src/main/resources/demo-data.sql
```

演示数据包含：

- 12 个车站
- 5 趟车
- 5 个出行日期
- 多条区间库存和座位类型

演示管理员账号：

```text
username: admin
password: admin123
```

演示查票接口：

```text
GET http://localhost:8080/api/tickets/search?departureStationId=1&arrivalStationId=2&travelDate=2026-06-20
```

## 面试讲解重点

可以重点讲这几部分：

- 数据库表设计：用户、车站、车次、经停车站、每日开行、库存、订单、支付。
- 订单状态流转：`PENDING_PAYMENT -> PAID / CANCELLED`。
- 库存防超卖：Redis Lua 原子锁票 + MySQL 原子更新兜底。
- 权限设计：JWT 登录态、`USER / ADMIN` 角色、`/api/admin/**` 管理员保护。
- 超时取消：定时任务扫描未支付订单并释放库存。
- 前后端联调：Vue 页面通过 `/api` 代理调用 Spring Boot 接口。

## 更多文档

- [项目计划](docs/project-plan.md)
- [API 设计](docs/api-design.md)
- [数据库设计](docs/database-design.md)
- [开发日志](docs/dev-log.md)
