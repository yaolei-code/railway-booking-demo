# Stage 15: Redis 库存缓存 + Lua 原子扣减

## 目标

在已有的数据库原子防超卖基础上，引入 Redis 作为库存缓存层，提升查票和锁票性能，同时用 Lua 脚本保证 Redis 内部的原子操作。

## 做了什么

### 1. 添加 Redis 依赖

- `spring-boot-starter-data-redis` + `commons-pool2`（Lettuce 连接池）
- `application.yml` 新增 Redis 连接配置，host/port 可通过 `REDIS_HOST` / `REDIS_PORT` 环境变量覆盖

### 2. 创建 RedisInventoryService

核心服务类，封装所有 Redis 库存操作：

- **数据结构**：Hash `inventory:{inventoryId}`，字段为 `availableCount`、`lockedCount`、`totalCount`、`price`
- **3 个 Lua 脚本**：`lockOne`（检查后扣减）、`confirmOne`（支付后清理锁定）、`releaseOne`（取消后归还）
- **启动预热**：`@PostConstruct` 从数据库全量加载库存到 Redis，Redis 不可用时静默跳过
- **懒加载**：搜索时 Redis key 不存在则回源 DB 并写回缓存
- **优雅降级**：lockOne 抛异常时调用方自动降级到纯 DB 模式；confirmOne / releaseOne 内部静默吞异常（DB 已做，Redis 同步失败不阻塞业务）

### 3. 改造下单流程（双层锁）

```
下单请求
  ├─ 第1层：Redis Lua 原子锁（~微秒）→ 售罄？→ 拒绝
  │          锁成功 ↓
  ├─ 第2层：MySQL 原子 UPDATE（~毫秒）→ 售罄？→ 回滚 Redis，拒绝
  │          锁成功 ↓
  └─ 创建订单 ✓
```

- Redis 可用时：绝大部分售罄请求在 Redis 层被拦截，只有少量请求打到 DB
- Redis 不可用时：静默跳过第一层，直接用 DB 锁，业务不受影响
- DB 锁失败时：回滚 Redis（`releaseOne`），保证两边数据一致

### 4. 改造支付、取消、搜索流程

- **支付**：DB 确认后 → Redis `confirmOne`（lockedCount - 1）
- **取消/超时**：DB 释放后 → Redis `releaseOne`（availableCount + 1, lockedCount - 1）
- **搜索**：优先读 Redis 可用票数（更快更实时），Redis miss 时懒加载写回，Redis 不可用时降级到 DB 原值
- **管理员创建库存**：DB 写入后同步 Redis

### 5. TicketOrderItem 新增 inventoryId 字段

- 之前取消/支付时要用 `scheduleId + route + seatType` 查库存，无法精准定位 Redis key
- 新增 `inventoryId` 字段后，取消/支付直接定位到对应 Redis Hash 做同步

### 6. Docker Compose 添加 Redis 服务

- 镜像：`redis:7-alpine`（轻量）
- 健康检查：`redis-cli ping`
- 后端启动依赖 Redis 健康检查通过

## 改动文件

| 文件 | 动作 |
|------|------|
| `backend/pom.xml` | 加 Redis 依赖 |
| `application.yml` | 加 Redis 连接配置 |
| `ticket/RedisInventoryService.java` | 新建：Lua 脚本 + 库存缓存 |
| `order/OrderService.java` | 改：下单双层锁 + DB 失败回滚 Redis |
| `payment/PaymentService.java` | 改：支付后同步 Redis |
| `ticket/TicketService.java` | 改：搜索 Redis 加速 + 管理员同步 |
| `ticket/TicketSearchResponse.java` | 改：record → class（需 setter） |
| `order/TicketOrderItem.java` | 改：加 inventoryId 字段 |
| `schema.sql` | 改：加 inventory_id 列 |
| `docker-compose.yml` | 改：加 Redis 服务 |
| `.env` | 改：加 REDIS_HOST |

## 技术要点（面试可讲）

1. **为什么 Redis + DB 双写**：Redis 做快车道拦截大部分请求，DB 做最终防线，两者优势互补
2. **为什么用 Lua 脚本**：Redis 命令"先查后改"不是原子操作，Lua 把多步操作合并成一条原子命令
3. **怎么保证一致性**：写操作同时写 DB 和 Redis（write-through），读操作 Redis 优先 + 懒加载兜底
4. **怎么降级**：每个 Redis 操作都有 try-catch 保护，挂了自动回到纯 DB 模式
5. **库存 key 设计**：Hash 而非 String，方便用一个 key 存多个字段，Lua 脚本也能原子操作

## 下一步

M6（并发与 Redis）完整收尾，进入 M8 收尾打磨（README、截图、简历要点）。
