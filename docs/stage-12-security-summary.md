# 阶段总结：Spring Security 角色权限第一版

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

本阶段把管理员接口从“路径上叫 admin”升级为“后端真正校验 ADMIN 角色”。

核心变化：

```text
请求进入后端
  -> Spring Security Filter Chain
  -> JWT 过滤器解析 token
  -> 查询用户角色
  -> /api/admin/** 要求 ADMIN
  -> Controller 执行业务逻辑
```

## 2. 本阶段新增和更新

后端：

- 新增 `spring-boot-starter-security`。
- 新增 `security/JwtAuthenticationFilter.java`。
- 新增 `security/SecurityConfig.java`。
- `users` 表新增 `role` 字段。
- 注册用户默认角色为 `USER`。
- 登录和当前用户接口返回 `role`。
- JWT 中写入 `role` claim。
- `/api/admin/**` 只允许 `ADMIN` 角色访问。

前端：

- 登录后保存当前用户的 `role`。
- 普通用户不显示管理台入口。
- 管理员才显示管理台。
- 前端请求会自动带上 JWT token，管理接口可通过后端权限校验。

演示数据：

```text
username: admin
password: admin123
role: ADMIN
```

## 3. 当前权限规则

公开接口：

```text
GET /api/health
POST /api/users/register
POST /api/users/login
GET /api/stations
GET /api/trains
GET /api/trains/{id}/stations
GET /api/tickets/search
```

登录用户接口：

```text
GET /api/users/me
/api/orders/**
```

管理员接口：

```text
/api/admin/**
```

## 4. 本地数据库迁移

如果本地数据库已经存在旧版 `users` 表，需要补字段：

```sql
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER email;
UPDATE users SET role = 'USER' WHERE role IS NULL OR role = '';
```

再执行演示数据脚本可写入管理员账号：

```bash
mysql -uroot -p050607 railway_booking < backend/src/main/resources/demo-data.sql
```

## 5. 已验证

后端：

```bash
cd backend
mvn test
```

结果：

```text
BUILD SUCCESS
```

前端：

```bash
cd frontend
npm run build
```

结果：

```text
vite build succeeded
```

## 6. 下一阶段建议

下一步已完成：

```text
前端路由拆分
```

用户端和管理端已经从一个页面拆成清晰路由：

```text
/tickets
/orders
/account
/admin
```

对应总结见：

```text
docs/stage-13-frontend-router-summary.md
```
