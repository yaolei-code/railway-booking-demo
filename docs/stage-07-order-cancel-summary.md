# 阶段总结：取消订单模块

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

取消订单模块用于完成“未支付订单不买了，把票释放回库存”的第一版流程。

前面已经完成：

```text
查票
  -> 下单
  -> 支付
```

现在新增：

```text
下单后未支付
  -> 取消订单
  -> 释放库存
```

## 2. 本阶段新增内容

本阶段没有新增数据库表。

更新代码：

```text
backend/src/main/java/com/example/railway/order/OrderService.java
backend/src/main/java/com/example/railway/order/OrderController.java
backend/api-tests.http
```

## 3. 当前接口

取消订单：

```text
POST /api/orders/{id}/cancel
```

请求头：

```text
Authorization: Bearer 登录接口返回的token
```

## 4. 当前业务规则

取消订单时：

- 必须带 JWT token。
- 订单必须存在。
- 订单必须属于当前登录用户。
- 订单状态必须是 `PENDING_PAYMENT`。
- 取消成功后订单状态改为 `CANCELLED`。
- 取消成功后写入 `cancelled_at`。
- 取消成功后库存 `locked_count` 减 1。
- 取消成功后库存 `available_count` 加 1。

已经支付的订单不能用这个接口取消。后续如果要退票退款，会单独做退款流程。

## 5. 给初学者的理解方式

可以把下单后的票理解成“临时占座”：

```text
下单：我先占住这张票，还没付钱。
支付：我付钱成功，这张票正式卖出。
取消：我不买了，把这张票还回库存。
```

库存变化：

```text
下单时：
available_count - 1
locked_count + 1

取消时：
available_count + 1
locked_count - 1
```

所以取消订单本质上是在“释放锁定库存”。

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

这一版取消订单模块还没有做：

- 超时自动取消。
- 支付后退款。
- 并发场景下的库存安全保护。

这些会在后续阶段逐步加入。

## 8. 下一阶段建议

下一步可以开发：

```text
未支付订单超时自动取消
```

也可以先做：

```text
数据库乐观锁 / Redis 防超卖
```

推荐先做超时取消，因为它直接补全订单生命周期。
