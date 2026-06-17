# 阶段总结：前端路由拆分第一版

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

本阶段把 Vue 前端从一个大页面拆成更接近企业项目的路由结构。

拆分前：

```text
App.vue
  -> 查票
  -> 订单
  -> 账号
  -> 管理台
```

拆分后：

```text
App.vue
  -> 布局壳
  -> RouterView

/tickets
/orders
/account
/admin
```

## 2. 本阶段新增依赖

```text
vue-router
```

## 3. 本阶段新增文件

```text
frontend/src/router/index.js
frontend/src/services/railwayStore.js
frontend/src/views/TicketSearchView.vue
frontend/src/views/OrdersView.vue
frontend/src/views/AccountView.vue
frontend/src/views/AdminView.vue
```

## 4. 本阶段更新文件

```text
frontend/src/App.vue
frontend/src/main.js
frontend/src/styles.css
frontend/package.json
frontend/package-lock.json
```

## 5. 当前路由设计

```text
/tickets  查票和下单
/orders   我的订单
/account  登录和注册
/admin    管理台
```

路由规则：

- `/tickets`：公开页面。
- `/account`：公开页面。
- `/orders`：需要登录。
- `/admin`：需要登录并且角色是 `ADMIN`。

## 6. 前端权限说明

前端路由守卫位于：

```text
frontend/src/router/index.js
```

访问 `/orders` 时，如果没有登录，会跳转到：

```text
/account
```

访问 `/admin` 时：

```text
未登录 -> /account
非 ADMIN -> /tickets
ADMIN -> 允许进入
```

注意：前端路由守卫只改善用户体验，不能替代后端权限。真正的管理员接口保护仍然由后端 Spring Security 完成：

```text
/api/admin/**
```

## 7. 共享状态

共享状态和 API 调用被抽到：

```text
frontend/src/services/railwayStore.js
```

这里集中维护：

- token
- 当前用户
- 车站列表
- 车次列表
- 车票结果
- 订单列表
- 登录、注册、查票、下单、支付、取消
- 管理台相关操作

后续如果要引入 Pinia，可以从这个文件平滑迁移。

## 8. 已验证

前端：

```bash
cd frontend
npm run build
```

结果：

```text
vite build succeeded
```

## 9. 下一阶段建议

下一步建议做：

```text
接口文档和 Swagger / Knife4j
```

理由：

- 后端接口已经比较完整。
- 权限边界已经有了。
- 前端也已经按页面拆开。
- 面试时可以直接展示 API 文档，讲清楚公开接口、用户接口和管理员接口。
