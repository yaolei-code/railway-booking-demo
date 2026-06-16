# 阶段总结：数据库原子扣库存防超卖

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

本阶段用于解决一个真实订票系统必须面对的问题：

```text
如果只剩 1 张票，但很多用户同时下单，系统不能卖出多张。
```

这类问题叫：

```text
超卖
```

本阶段先不用 Redis，而是用数据库原子更新做第一版防护。

## 2. 本阶段更新内容

更新代码：

```text
backend/src/main/java/com/example/railway/ticket/TicketInventoryMapper.java
backend/src/main/java/com/example/railway/order/OrderService.java
backend/src/main/java/com/example/railway/payment/PaymentService.java
```

## 3. 旧流程的问题

旧流程是：

```text
查询库存
  -> Java 判断 available_count 是否大于 0
  -> Java 修改 available_count 和 locked_count
  -> 保存回数据库
```

在单人操作时没问题。

但如果多人同时下单，可能出现：

```text
用户 A 查到还剩 1 张
用户 B 也查到还剩 1 张
用户 A 下单成功
用户 B 也下单成功
```

这样就可能超卖。

## 4. 新流程怎么解决

现在下单时改成数据库一条 SQL：

```sql
UPDATE ticket_inventory
SET available_count = available_count - 1,
    locked_count = locked_count + 1
WHERE id = ?
  AND available_count > 0
```

重点是：

```text
检查 available_count > 0
扣减 available_count
增加 locked_count
```

这三件事交给数据库在一条语句里完成。

如果成功更新 1 行，说明抢到票。

如果成功更新 0 行，说明没票了。

## 5. 支付和取消也做了对应调整

支付成功时：

```text
locked_count - 1
```

取消订单时：

```text
available_count + 1
locked_count - 1
```

这些也改成了 Mapper 里的原子 SQL，避免先查再改带来的并发风险。

## 6. 给初学者的理解方式

旧方式像这样：

```text
我先看一下柜台还有没有票
看到了
我回头告诉售票员我要买
```

中间有时间差，别人可能也看到了。

新方式像这样：

```text
我直接对售票员说：如果还有票，就立刻帮我扣一张。
```

有没有票、扣不扣成功，由售票员当场一次完成。

这里的“售票员”就是数据库。

## 7. 已验证

已执行：

```bash
cd backend
mvn test
```

结果：

```text
BUILD SUCCESS
```

## 8. 当前限制

这一版还没有做：

- 压测并发下单。
- Redis 库存缓存。
- Redis Lua 原子扣减。
- 数据库版本号乐观锁字段。

不过对于当前学习阶段，数据库条件更新已经比“先查再改”安全很多。

## 9. 下一阶段建议

下一步可以做：

```text
基础集成测试 / 接口验证脚本
```

或者继续做：

```text
Vue 前端第一版
```

如果想让项目更适合简历，建议先补一些测试和 API 文档，再进入前端。
