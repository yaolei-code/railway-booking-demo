# 铁路订票系统 Railway Booking Demo

> 一个面向 Java 后端 / 全栈岗位的完整练手项目 —— 从数据库设计到 Docker 部署，覆盖真实订票业务全链路。

## 项目亮点

- 🚄 **完整业务链路**：用户 → 查票 → 下单 → 支付 → 取消 → 超时回收，状态流转清晰
- 🛡️ **双重防超卖**：Redis Lua 原子锁票（快车道）+ MySQL 原子 UPDATE 兜底（最终防线），Redis 不可用时自动降级
- 🔐 **JWT + 角色权限**：Spring Security 拦截 `/api/admin/**`，普通用户 `USER` 和管理员 `ADMIN` 分离
- ⏱️ **定时超时取消**：`@Scheduled` 每分钟扫描超时未支付订单，自动释放锁定库存
- 🐳 **一键部署**：`docker compose up -d` 启动 MySQL + Redis + 后端 + 前端，开箱即用
- 📄 **接口文档**：Knife4j / Swagger 在线调试，不用 Postman
- 🎨 **Vue 3 前端**：Element Plus UI，四页面 + 路由守卫，前后端通过 Nginx 代理联调

## 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Compose                           │
│                                                             │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐ │
│  │  MySQL   │   │  Redis   │   │  Backend │   │ Frontend │ │
│  │  :3306   │   │  :6379   │   │  :8080   │   │  :5173   │ │
│  │  数据    │   │  缓存    │   │ Spring   │   │  Nginx   │ │
│  │  持久化  │   │  库存    │   │ Boot 3   │   │  Vue 3   │ │
│  └────┬─────┘   └────┬─────┘   └────┬─────┘   └────┬─────┘ │
│       │              │              │              │        │
│       └──────────────┴──────┬───────┘              │        │
│                             │  /api 代理 ──────────┘        │
└─────────────────────────────┘                               │
                              │
                              ▼
                      ┌──────────────┐
                      │   浏览器      │
                      │ localhost:5173│
                      └──────────────┘
```

**库存防超卖双层锁流程：**

```
下单请求 ──→ Redis Lua 原子锁（~微秒）──→ 售罄？──→ 直接拒绝
                  │ 锁成功
                  ▼
            MySQL 原子 UPDATE （~毫秒）──→ 售罄？──→ 回滚 Redis，拒绝
                  │ 锁成功
                  ▼
              创建订单 ✓
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Java 17 · Spring Boot 3 · Spring Security |
| 认证 | JWT（jjwt） |
| 持久层 | MyBatis-Plus · MySQL 8.0 |
| 缓存 | Redis 7 · Lettuce 连接池 · Lua 脚本 |
| 前端 | Vue 3 · Vite · Element Plus · Vue Router |
| 文档 | Knife4j · Swagger · OpenAPI 3 |
| 部署 | Docker · Docker Compose · Nginx |

## 已实现功能

| 模块 | 功能 |
|------|------|
| 用户 | 注册、登录、JWT 签发、个人信息查询、角色区分 |
| 车站 | 列表查询、管理员增删改、按城市/名称搜索 |
| 车次 | 列表、详情、经停车站管理、时刻表 |
| 余票 | 按起止站 + 日期 + 座位类型搜索，Redis 缓存加速 |
| 订单 | 创建（自动锁库存）、列表、详情、取消、状态流转 |
| 支付 | 模拟支付、支付记录、支付后确认库存 |
| 定时 | 每分钟扫描超时未支付订单，自动取消并释放库存 |
| 权限 | USER/ADMIN 角色、`/api/admin/**` 拦截、前端路由守卫 |
| 文档 | Knife4j + Swagger 在线调试 |
| 部署 | Docker Compose 一键启动四服务 |

## 快速启动

```bash
# 1. 克隆项目
git clone <repo-url> && cd railway-booking-demo

# 2. 一键启动（首次需下载镜像，约 3-5 分钟）
docker compose up -d

# 3. 打开浏览器
#    前端:      http://localhost:5173
#    接口文档:  http://localhost:8080/doc.html
#    管理员:    admin / admin123
```

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:5173 |
| Knife4j 接口文档 | http://localhost:8080/doc.html |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| 健康检查 | http://localhost:8080/api/health |

停止：`docker compose down`　｜　重建：`docker compose up -d --build`

## 项目截图

> 启动项目后截取以下页面替换此处占位图。

| 页面 | 截图 |
|------|------|
| 查票下单页 | ![ticket-search](docs/screenshots/ticket-search.png) |
| 我的订单页 | ![orders](docs/screenshots/orders.png) |
| 登录注册页 | ![account](docs/screenshots/account.png) |
| 管理台 | ![admin](docs/screenshots/admin.png) |
| Knife4j 文档 | ![knife4j](docs/screenshots/knife4j.png) |

## 演示数据

Docker Compose 自动建表并灌入演示数据（12 个车站 · 5 趟车 · 5 个出行日期 · 多条库存）：

```text
管理员账号: admin / admin123
查票示例:   GET /api/tickets/search?departureStationId=1&arrivalStationId=2&travelDate=2026-06-20
```

## 本地开发

```bash
# 后端（需要本地 MySQL 和 Redis）
cd backend && mvn spring-boot:run

# 前端
cd frontend && npm install && npm run dev
```

## 📂 更多文档

| 文档 | 说明 |
|------|------|
| [项目计划](docs/project-plan.md) | 完整里程碑和任务拆解 |
| [数据库设计](docs/database-design.md) | 14 张表 ER 说明 |
| [API 设计](docs/api-design.md) | 所有接口定义 |
| [开发日志](docs/dev-log.md) | 每个阶段的实现记录 |
| [简历要点](docs/resume-bullet-points.md) | 可直接复制到简历的项目描述 |
