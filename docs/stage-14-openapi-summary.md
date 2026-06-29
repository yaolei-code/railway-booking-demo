# 阶段总结：Knife4j / OpenAPI 接口文档第一版

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

本阶段把后端接口接入 Knife4j / OpenAPI，让项目不只“有接口”，还可以在浏览器里按模块查看和调试接口。

这一步适合面试展示：

```text
后端接口
  -> OpenAPI 自动生成接口元数据
  -> Knife4j / Swagger UI 可视化展示
  -> 登录后用 JWT 调试用户和管理员接口
```

## 2. 本阶段新增和更新

后端：

- 新增 `knife4j-openapi3-jakarta-spring-boot-starter`。
- 新增 `config/OpenApiConfig.java`。
- 配置 OpenAPI 标题、描述、版本和 JWT Bearer 认证方案。
- 在主要 Controller 上增加接口分组和接口说明。
- 在需要登录或 ADMIN 角色的接口上标记 `bearerAuth`。
- 在请求 DTO 上增加示例值，方便文档页面直接查看请求格式。

安全配置：

- 放行 `/doc.html`。
- 放行 `/swagger-ui.html`。
- 放行 `/swagger-ui/**`。
- 放行 `/v3/api-docs/**`。
- 放行 `/webjars/**`。

测试：

- `SecurityAuthorizationTests` 新增 `/v3/api-docs` 公开访问测试。

## 3. 当前访问入口

启动后端：

```bash
cd backend
mvn spring-boot:run
```

访问接口文档：

```text
Knife4j UI: http://localhost:8080/doc.html
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
```

## 4. JWT 调试方式

先调用登录接口：

```text
POST /api/users/login
```

拿到返回里的 `token` 后，在 Knife4j / Swagger UI 的认证入口填写 JWT token，再调试：

```text
GET /api/users/me
/api/orders/**
/api/admin/**
```

管理员演示账号：

```text
username: admin
password: admin123
role: ADMIN
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

测试数量：

```text
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

## 6. 下一阶段建议

下一步建议做项目收尾打磨：

- 改 README 为更像简历项目首页的结构。
- 写面试讲解提纲：业务流程、权限设计、库存防超卖、订单状态机。
- 补前端截图和接口文档截图。
- 加 Docker Compose，让 MySQL、后端、前端能一键启动。
