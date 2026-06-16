# 阶段总结：查票模块

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

查票模块用于回答用户最关心的问题：

```text
某一天，从 A 站到 B 站，有哪些车可以坐？
```

前面已经完成：

```text
用户模块：系统知道谁在使用
车站模块：系统知道有哪些车站
车次模块：系统知道有哪些车，以及每趟车经过哪些站
```

现在新增：

```text
查票模块：系统可以按出发站、到达站、日期查询余票
```

## 2. 本阶段新增表

```text
train_daily_schedules
ticket_inventory
```

`train_daily_schedules` 表示某一天某趟车是否开行。

例如：

```text
G101 在 2026-06-20 OPEN
```

`ticket_inventory` 表示某一天、某趟车、某个区间、某个座位类型还有多少票。

例如：

```text
G101 2026-06-20 北京南 -> 上海虹桥 SECOND_CLASS 余票 100 票价 553.00
```

## 3. 本阶段新增代码

新增后端包：

```text
backend/src/main/java/com/example/railway/ticket
```

核心文件：

```text
TrainDailySchedule.java
TicketInventory.java
TrainDailyScheduleMapper.java
TicketInventoryMapper.java
ScheduleRequest.java
ScheduleResponse.java
InventoryRequest.java
InventoryResponse.java
TicketSearchResponse.java
TicketService.java
TicketController.java
```

## 4. 当前接口

创建每日车次：

```text
POST /api/admin/schedules
```

创建余票库存：

```text
POST /api/admin/inventory
```

查票：

```text
GET /api/tickets/search?departureStationId=1&arrivalStationId=2&travelDate=2026-06-20
```

按座位类型查票：

```text
GET /api/tickets/search?departureStationId=1&arrivalStationId=2&travelDate=2026-06-20&seatType=SECOND_CLASS
```

## 5. 返回结果包含什么

查票接口会返回：

```text
车次号
车次类型
出发站
到达站
出发时间
到达时间
历时分钟数
座位类型
余票数量
票价
```

## 6. 当前业务规则

创建每日车次时：

- 车次必须存在。
- 同一趟车同一天不能重复创建。

创建库存时：

- 每日车次必须存在。
- 出发站和到达站必须在这趟车的路线里。
- 出发站必须在到达站前面。
- 可用票数加锁定票数不能超过总票数。
- 同一个车次日期、区间、座位类型不能重复创建库存。

查票时：

- 出发站和到达站不能相同。
- 只返回余票大于 0 的结果。
- 只返回 `OPEN` 的每日车次。
- 只返回 `ACTIVE` 的车次。
- 会根据经停站时间计算历时。

## 7. 本地演示数据

本地 MySQL 已添加一组演示数据：

```text
北京南 -> 上海虹桥
G101
2026-06-20
SECOND_CLASS
余票 100
票价 553.00
```

启动后端后，可以访问：

```text
http://localhost:8080/api/tickets/search?departureStationId=1&arrivalStationId=2&travelDate=2026-06-20
```

如果数据没有被手动改动，应该能看到 G101 的查票结果。

## 8. 给初学者的理解方式

查票不是只查一张表，而是把几张表串起来：

```text
stations：知道北京南、上海虹桥是什么站
trains：知道 G101 是什么车
train_stations：知道 G101 是否经过北京南和上海虹桥
train_daily_schedules：知道 G101 在 2026-06-20 是否开行
ticket_inventory：知道这一天这个区间还有多少票
```

所以查票模块是第一个真正体现“业务组合查询”的模块。

## 9. 已验证

已执行：

```bash
cd backend
mvn test
```

结果：

```text
BUILD SUCCESS
```

本地 MySQL 数据库 `railway_booking` 中也已创建 `train_daily_schedules` 和 `ticket_inventory` 表。

## 10. 下一阶段建议

下一步可以开发：

```text
订单模块
```

推荐顺序：

1. 创建订单表 `ticket_orders`。
2. 创建订单明细表 `ticket_order_items`。
3. 实现创建订单接口。
4. 下单时扣减或锁定库存。
5. 实现订单列表和订单详情接口。
