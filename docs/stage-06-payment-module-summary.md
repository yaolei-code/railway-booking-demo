# 阶段总结：模拟支付模块

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

模拟支付模块用于完成“待支付订单变成已支付订单”的第一版流程。

前面已经完成：

```text
查票
  -> 下单
  -> 生成 PENDING_PAYMENT 订单
```

现在新增：

```text
支付
  -> 生成支付流水
  -> 订单状态改为 PAID
```

## 2. 本阶段新增表

```text
payments
```

`payments` 保存支付流水：

```text
订单 ID
支付流水号
支付金额
支付状态
支付时间
```

注意：这里是模拟支付，不会真的调用微信、支付宝或银行卡接口。

## 3. 本阶段新增代码

新增后端包：

```text
backend/src/main/java/com/example/railway/payment
```

核心文件：

```text
Payment.java
PaymentMapper.java
PaymentResponse.java
PaymentService.java
PaymentController.java
```

## 4. 当前接口

支付订单：

```text
POST /api/orders/{id}/pay
```

请求头：

```text
Authorization: Bearer 登录接口返回的token
```

## 5. 当前业务规则

支付订单时：

- 必须带 JWT token。
- 订单必须存在。
- 订单必须属于当前登录用户。
- 订单状态必须是 `PENDING_PAYMENT`。
- 已经支付过的订单不能重复支付。
- 支付成功后创建一条 `payments` 记录。
- 支付成功后订单状态改为 `PAID`。
- 支付成功后订单写入 `paid_at`。
- 支付成功后库存的 `locked_count` 减 1。

## 6. 给初学者的理解方式

可以把下单和支付拆成两步：

```text
下单：我先占住这张票，还没付钱。
支付：我付钱成功，这张票正式属于我。
```

库存字段可以这样理解：

```text
available_count：还能被别人买的票
locked_count：已经被下单锁住、但还没支付完成的票
```

所以：

```text
下单时：
available_count - 1
locked_count + 1

支付时：
available_count 不变
locked_count - 1
```

因为支付时这张票早就从可售库存里拿走了，不应该再减一次 `available_count`。

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

本地 MySQL 数据库 `railway_booking` 中也已创建 `payments` 表。

## 8. 当前限制

这一版支付模块还没有做：

- 第三方真实支付。
- 支付失败场景。
- 取消订单。
- 未支付超时自动取消。
- Redis 并发防超卖。

这些会在后续阶段逐步加入。

## 9. 下一阶段建议

下一步可以开发：

```text
取消订单模块
```

推荐顺序：

1. 实现取消未支付订单接口。
2. 取消时把订单状态改为 `CANCELLED`。
3. 取消时把 `locked_count` 减 1。
4. 取消时把 `available_count` 加 1。
5. 后续再加超时自动取消。
