# 阶段总结：未支付订单超时自动取消

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

未支付订单超时自动取消用于处理这种场景：

```text
用户下单了
  -> 票被锁住
  -> 但用户一直没有支付
  -> 系统自动取消订单
  -> 把票还回库存
```

这一步补全了订单生命周期里很重要的一环。

## 2. 本阶段新增内容

本阶段没有新增数据库表。

更新代码：

```text
backend/src/main/java/com/example/railway/RailwayBookingApplication.java
backend/src/main/java/com/example/railway/order/OrderService.java
backend/src/main/resources/application.yml
```

## 3. 当前配置

```yaml
app:
  order:
    payment-timeout-minutes: 15
    timeout-initial-delay-ms: 60000
    timeout-scan-ms: 60000
```

含义：

- `payment-timeout-minutes: 15`：订单创建超过 15 分钟还没支付，就算超时。
- `timeout-initial-delay-ms: 60000`：应用启动后等 60 秒再第一次扫描。
- `timeout-scan-ms: 60000`：之后每 60 秒扫描一次。

## 4. 当前业务规则

定时任务会查找：

```text
status = PENDING_PAYMENT
created_at <= 当前时间 - 15 分钟
```

找到后执行：

```text
locked_count - 1
available_count + 1
订单状态改为 CANCELLED
写入 cancelled_at
```

## 5. 给初学者的理解方式

可以把这个功能理解成“后台值班员”：

```text
用户手动取消：用户自己说我不买了。
超时自动取消：用户一直没付钱，系统帮他取消。
```

两者释放库存的动作是一样的：

```text
available_count + 1
locked_count - 1
```

区别只是触发方式不同：

```text
手动取消：用户调用接口触发。
自动取消：系统定时任务触发。
```

## 6. 已验证

已执行：

```bash
cd backend
mvn test
```

结果：

```text
BUILD SUCCESS
```

## 7. 当前限制

这一版还没有做：

- 多实例部署下的分布式定时任务锁。
- 并发扣库存的数据库乐观锁。
- Redis 库存缓存和 Lua 原子扣减。

这些属于并发安全和生产级增强，会在后续阶段逐步加入。

## 8. 下一阶段建议

下一步建议做：

```text
并发防超卖第一版
```

可以先用数据库乐观锁实现，比直接上 Redis 更适合当前学习阶段。
