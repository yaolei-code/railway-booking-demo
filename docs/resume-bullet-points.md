# 简历项目描述（可直接复制）

以下内容按"项目概述 → 个人职责 → 技术亮点"三段式组织，适合放在简历的「项目经历」板块。

---

## 项目经历

**铁路订票系统** | Java 后端开发 | 2026.06 - 2026.07

> 独立完成一个铁路订票系统，覆盖用户认证、车票查询、订单支付、库存管理、管理后台、Docker 部署等完整业务链路。

### 项目概述

基于 Spring Boot 3 + Vue 3 构建的铁路订票全栈项目，包含用户注册登录（JWT + Spring Security 角色鉴权）、按起止站和日期搜索车次余票、下单锁库存、模拟支付、订单取消与超时回收、管理员后台等核心功能，支持 Docker Compose 一键部署。

### 工作内容

- 设计 10 张 MySQL 业务表（用户、车站、车次、经停车站、每日开行、库存、订单、支付等），使用 MyBatis-Plus 实现数据访问层
- 实现 JWT 认证 + Spring Security 角色权限，区分普通用户和管理员，前端路由守卫配合后端接口拦截
- 使用 **Redis Lua 脚本实现原子锁库存**，与 MySQL 原子 UPDATE 组成双层防超卖机制，Redis 不可用时自动降级到数据库模式
- 使用 Spring `@Scheduled` 定时任务每分钟扫描超时未支付订单，自动取消并释放锁定库存
- 搭建 Vue 3 前端（查票、订单、登录、管理台四页面），Nginx 代理 `/api` 到后端，Docker Compose 编排 MySQL + Redis + 后端 + 前端一键启动
- 接入 Knife4j / OpenAPI 生成在线接口文档

### 技术亮点（面试可展开）

1. **Redis Lua 原子锁票**：用 Lua 脚本把"查询 + 扣减"合并为一个原子操作，避免并发超卖。同时保留 MySQL 原子 UPDATE 作为最终防线，Redis 挂了自动降级。
2. **订单状态流转**：`PENDING_PAYMENT → PAID / CANCELLED`，每个状态变更同步更新数据库和 Redis 的锁定/可用计数。
3. **定时超时取消**：`@Scheduled` 每分钟扫描创建超过 15 分钟的待支付订单，自动取消并回退库存。
4. **Docker Compose 一键部署**：编排 MySQL、Redis、后端、前端四个容器，含健康检查和服务依赖，新环境 `docker compose up -d` 即可运行。

---

## 简短版（一行式，适合放技能总结旁边）

> 独立开发铁路订票全栈项目：Spring Boot 3 + Vue 3 + MySQL + Redis + Docker，实现 JWT 鉴权、Redis Lua 防超卖、订单超时自动取消、Docker 一键部署。

---

## 技术栈速查（用于简历技能栏）

```
后端: Java 17, Spring Boot 3, Spring Security, JWT, MyBatis-Plus, MySQL, Redis (Lua)
前端: Vue 3, Element Plus, Vue Router, Vite
工具: Maven, Git, Docker, Docker Compose, Nginx, Knife4j/OpenAPI
```
