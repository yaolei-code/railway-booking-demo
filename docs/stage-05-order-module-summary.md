# 阶段总结：订单模块

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

订单模块用于完成“查到票之后下单”的第一版流程。

前面已经完成：

```text
用户模块：系统知道谁在使用
车站模块：系统知道有哪些车站
车次模块：系统知道有哪些车，以及每趟车经过哪些站
查票模块：系统知道某天某区间还有多少票
```

现在新增：

```text
订单模块：用户可以把一张余票变成自己的待支付订单
```

## 2. 本阶段新增表

```text
ticket_orders
ticket_order_items
```

`ticket_orders` 是订单主表，保存一笔订单的总体信息：

```text
订单号
用户 ID
订单状态
总金额
创建时间
```

`ticket_order_items` 是订单明细表，保存具体车票信息：

```text
乘车人
证件号
每日车次
出发站
到达站
座位类型
票价
```

## 3. 本阶段新增代码

新增后端包：

```text
backend/src/main/java/com/example/railway/order
```

核心文件：

```text
TicketOrder.java
TicketOrderItem.java
TicketOrderMapper.java
TicketOrderItemMapper.java
CreateOrderRequest.java
OrderItemResponse.java
OrderResponse.java
OrderService.java
OrderController.java
```

## 4. 当前接口

创建订单：

```text
POST /api/orders
```

查询我的订单：

```text
GET /api/orders
```

查询我的订单详情：

```text
GET /api/orders/{id}
```

这些接口都需要请求头：

```text
Authorization: Bearer 登录接口返回的token
```

## 5. 请求示例

创建订单：

```json
{
  "inventoryId": 1,
  "passengerName": "张三",
  "passengerIdNo": "110101199001011234"
}
```

## 6. 当前业务规则

创建订单时：

- 必须带 JWT token。
- 库存 `inventoryId` 必须存在。
- 余票 `available_count` 必须大于 0。
- 下单成功后，`available_count` 减 1。
- 下单成功后，`locked_count` 加 1。
- 创建订单状态为 `PENDING_PAYMENT`。
- 创建一条订单明细。

查询订单时：

- 只能查询当前登录用户自己的订单。
- 不能通过订单 ID 查看别人的订单。

## 7. 给初学者的理解方式

订单为什么要分两张表？

可以把 `ticket_orders` 理解成“小票封面”：

```text
订单号、谁买的、总价、状态
```

可以把 `ticket_order_items` 理解成“小票里的商品明细”：

```text
乘车人、哪趟车、从哪到哪、座位类型、价格
```

现在第一版一笔订单只有一张票，但真实系统里一笔订单可能有多张票，所以提前分成主表和明细表更合理。

## 8. 已验证

已执行：

```bash
cd backend
mvn test
```

结果：

```text
BUILD SUCCESS
```

本地 MySQL 数据库 `railway_booking` 中也已创建 `ticket_orders` 和 `ticket_order_items` 表。

## 9. 当前限制

这一版订单模块还没有做：

- 模拟支付。
- 取消订单。
- 未支付超时自动取消。
- Redis 并发防超卖。
- 数据库乐观锁防并发扣库存。

这些会在后续阶段逐步加入。

## 10. 下一阶段建议

下一步可以开发：

```text
模拟支付模块
```

推荐顺序：

1. 创建支付表 `payments`。
2. 实现订单支付接口。
3. 支付成功后把订单状态从 `PENDING_PAYMENT` 改为 `PAID`。
4. 记录支付流水。
