# 数据库设计说明

这个文档先描述铁路订票系统第一版需要哪些表，以及每张表负责保存什么信息。后续写注册、登录、查票、下单接口时，代码都会围绕这些表展开。

## 1. 设计目标

铁路订票系统至少要回答这些问题：

- 用户是谁，能不能登录？
- 有哪些车站？
- 有哪些车次？
- 一趟车经过哪些站？
- 某天某趟车还有多少票？
- 用户下了什么订单？
- 订单有没有支付、取消或退款？

所以数据库不是随便建表，而是把业务里的“东西”拆成一张张表。

## 2. 核心表

### 2.1 users 用户表

保存注册用户的信息。

| 字段 | 含义 |
| --- | --- |
| id | 用户 ID，主键 |
| username | 用户名 |
| password_hash | 加密后的密码 |
| phone | 手机号 |
| email | 邮箱 |
| status | 用户状态，例如 ENABLED、DISABLED |
| created_at | 创建时间 |
| updated_at | 更新时间 |

说明：密码不能明文保存，必须保存加密后的 `password_hash`。

### 2.2 roles 角色表

保存系统角色。

| 字段 | 含义 |
| --- | --- |
| id | 角色 ID，主键 |
| code | 角色编码，例如 USER、ADMIN |
| name | 角色名称 |

### 2.3 user_roles 用户角色关联表

表示一个用户有哪些角色。

| 字段 | 含义 |
| --- | --- |
| user_id | 用户 ID |
| role_id | 角色 ID |

说明：普通用户是 `USER`，管理员是 `ADMIN`。

### 2.4 stations 车站表

保存车站信息。

| 字段 | 含义 |
| --- | --- |
| id | 车站 ID，主键 |
| name | 车站名，例如 北京南 |
| city | 所在城市 |
| code | 车站编码 |
| created_at | 创建时间 |
| updated_at | 更新时间 |

### 2.5 trains 车次表

保存列车的基本信息。

| 字段 | 含义 |
| --- | --- |
| id | 车次 ID，主键 |
| train_no | 车次号，例如 G101 |
| train_type | 车次类型，例如 G、D、K |
| status | 状态，例如 ACTIVE、INACTIVE |
| created_at | 创建时间 |
| updated_at | 更新时间 |

### 2.6 train_stations 经停车站表

保存一趟车经过哪些站，以及到站、发车时间。

| 字段 | 含义 |
| --- | --- |
| id | 主键 |
| train_id | 车次 ID |
| station_id | 车站 ID |
| stop_order | 第几站 |
| arrival_time | 到站时间 |
| departure_time | 发车时间 |

说明：比如 G101 从北京南到上海虹桥，中间经过济南西、南京南，这些站都会在这张表里。

### 2.7 train_daily_schedules 每日车次表

保存某一天是否开行某趟车。

| 字段 | 含义 |
| --- | --- |
| id | 主键 |
| train_id | 车次 ID |
| travel_date | 出行日期 |
| status | 状态，例如 OPEN、CANCELLED |

说明：`trains` 表保存“长期存在的车次”，这张表保存“某一天实际开不开”。

### 2.8 ticket_inventory 余票库存表

保存某天某趟车某个座位类型的余票数量。

| 字段 | 含义 |
| --- | --- |
| id | 主键 |
| schedule_id | 每日车次 ID |
| departure_station_id | 出发站 ID |
| arrival_station_id | 到达站 ID |
| seat_type | 座位类型，例如 SECOND_CLASS、FIRST_CLASS |
| total_count | 总票数 |
| available_count | 剩余票数 |
| locked_count | 已锁定票数 |

说明：下单时会先锁票或扣票，防止多人同时买到同一张票。

### 2.9 ticket_orders 订单表

保存订单主信息。

| 字段 | 含义 |
| --- | --- |
| id | 订单 ID，主键 |
| order_no | 订单号 |
| user_id | 下单用户 ID |
| status | 订单状态 |
| total_amount | 总金额 |
| created_at | 创建时间 |
| paid_at | 支付时间 |
| cancelled_at | 取消时间 |

订单状态第一版先设计为：

- `PENDING_PAYMENT`：待支付
- `PAID`：已支付
- `CANCELLED`：已取消
- `REFUNDED`：已退款

### 2.10 ticket_order_items 订单明细表

保存一张订单里的具体车票。

| 字段 | 含义 |
| --- | --- |
| id | 主键 |
| order_id | 订单 ID |
| passenger_name | 乘车人姓名 |
| passenger_id_no | 乘车人证件号 |
| schedule_id | 每日车次 ID |
| departure_station_id | 出发站 ID |
| arrival_station_id | 到达站 ID |
| seat_type | 座位类型 |
| price | 票价 |

说明：一张订单可以包含多张票，所以订单主表和订单明细表要分开。

### 2.11 payments 支付表

保存模拟支付记录。

| 字段 | 含义 |
| --- | --- |
| id | 支付 ID，主键 |
| order_id | 订单 ID |
| payment_no | 支付流水号 |
| amount | 支付金额 |
| status | 支付状态 |
| paid_at | 支付时间 |

## 3. 表之间的关系

```text
users 1 --- n ticket_orders
ticket_orders 1 --- n ticket_order_items
ticket_orders 1 --- 1 payments

trains 1 --- n train_stations
trains 1 --- n train_daily_schedules
train_daily_schedules 1 --- n ticket_inventory

users n --- n roles
```

## 4. 下一步编码顺序

第一版不要一口气做完整系统，按这个顺序推进：

1. 先做用户注册和登录，只用到 `users`、`roles`、`user_roles`。
2. 再做车站管理，只用到 `stations`。
3. 再做车次和经停车站，只用到 `trains`、`train_stations`。
4. 再做查票，只用到 `train_daily_schedules`、`ticket_inventory`。
5. 最后做下单、支付和取消，只用到订单相关表。

## 5. 给初学者的理解方式

可以先把数据库理解成很多张 Excel 表：

- Entity 是 Java 里的“一行数据”。
- DAO 或 Mapper 是“查表的人”。
- Service 是“判断业务规则的人”。
- Controller 是“接收用户请求的人”。

后面写代码时，基本就是：

```text
浏览器请求
  -> Controller
  -> Service
  -> Mapper/DAO
  -> MySQL
```

