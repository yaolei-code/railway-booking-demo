# 阶段总结：前端管理页第一版

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

本阶段在 Vue 前端第一版用户链路基础上，补齐管理员操作入口。

第一版管理页主要让已有后端管理接口可以在浏览器里点击操作：

```text
车站维护
  -> 车次维护
  -> 经停车站维护
  -> 每日开行创建
  -> 库存创建
```

## 2. 本阶段更新文件

```text
frontend/src/App.vue
README.md
docs/project-plan.md
docs/dev-log.md
```

新增本文档：

```text
docs/stage-11-admin-frontend-summary.md
```

## 3. 当前前端管理功能

当前管理台可以：

- 创建、编辑、删除车站。
- 创建、编辑、删除车次。
- 选择车次并维护经停车站。
- 创建每日车次开行记录。
- 创建区间库存，包含座位类型、总票数、可售票数、锁定票数和价格。

## 4. 对应后端接口

车站：

```text
GET /api/stations
POST /api/admin/stations
PUT /api/admin/stations/{id}
DELETE /api/admin/stations/{id}
```

车次和经停：

```text
GET /api/trains
POST /api/admin/trains
PUT /api/admin/trains/{id}
DELETE /api/admin/trains/{id}
GET /api/trains/{id}/stations
PUT /api/admin/trains/{id}/stations
```

开行和库存：

```text
POST /api/admin/schedules
POST /api/admin/inventory
```

## 5. 当前限制

这一版管理页还没有做：

- 管理员角色权限校验。
- 独立路由拆分。
- schedule 和 inventory 的列表查询、编辑、删除。
- 表单必填校验和更细的错误提示。
- 操作审计日志。

其中最重要的下一步是：

```text
Spring Security 角色权限
```

后端接入角色权限后，`/api/admin/**` 才会真正只允许管理员访问。

## 6. 已验证

前端构建：

```bash
cd frontend
npm run build
```

结果：

```text
vite build succeeded
```
