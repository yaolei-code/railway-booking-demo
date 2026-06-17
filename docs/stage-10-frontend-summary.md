# 阶段总结：Vue 前端第一版

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

本阶段开始做前端页面，让后端接口从“只能用接口测试工具调用”变成“可以在浏览器里点击操作”。

第一版前端不是最终成品页面，而是把核心业务链路先串起来：

```text
注册 / 登录
  -> 查票
  -> 下单
  -> 支付
  -> 取消订单
```

## 2. 本阶段新增目录

```text
frontend
```

核心文件：

```text
frontend/package.json
frontend/index.html
frontend/vite.config.js
frontend/src/main.js
frontend/src/App.vue
frontend/src/styles.css
```

## 3. 使用的前端技术

```text
Vue 3
Vite
Element Plus
lucide-vue-next
```

## 4. 当前页面功能

当前前端页面可以：

- 注册用户。
- 登录用户。
- 保存 JWT token。
- 查询车站。
- 查询车票。
- 创建订单。
- 查看我的订单。
- 支付订单。
- 取消订单。

## 5. 启动方式

先启动后端：

```bash
cd backend
mvn spring-boot:run
```

再启动前端：

```bash
cd frontend
npm install
npm run dev
```

访问：

```text
http://localhost:5173
```

## 6. 前后端如何连接

前端代码里请求的是：

```text
/api/...
```

Vite 配置了代理：

```text
/api -> http://localhost:8080
```

所以浏览器访问前端 `5173` 端口时，请求会被 Vite 转发到后端 `8080` 端口。

## 7. 当前演示数据

本地可以先执行：

```bash
mysql -uroot -p050607 railway_booking -e "source backend/src/main/resources/demo-data.sql"
```

然后前端可以查到多条演示车票，例如：

```text
北京南 -> 上海虹桥
G101
2026-06-20
SECOND_CLASS
```

当前演示数据包含 12 个车站、5 趟车、5 个出行日期和多种座位库存。

## 8. 已验证

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

## 9. 当前限制

这一版前端还没有做：

- 管理员专用车站管理页面。
- 管理员专用车次管理页面。
- 路由拆分。
- Pinia 状态管理。
- 更完整的错误空状态。
- 生产级登录过期处理。
- 移动端深度打磨。

## 10. 下一阶段建议

下一步可以做：

```text
前端管理页第一版
```

也可以先做：

```text
Spring Security 角色权限
```

如果目标是简历展示，建议先把前端核心页面跑通并截图，再补管理员页。
