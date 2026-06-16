# Development Log

## 2026-06-15

### What We Did

Initialized the project repository and created the first planning documents:

- `README.md`
- `docs/project-plan.md`

Created the first Spring Boot backend skeleton:

- `backend/pom.xml`
- `backend/src/main/java/com/example/railway/RailwayBookingApplication.java`
- `backend/src/main/java/com/example/railway/common/ApiResponse.java`
- `backend/src/main/java/com/example/railway/common/GlobalExceptionHandler.java`
- `backend/src/main/java/com/example/railway/health/HealthController.java`
- `backend/src/main/resources/application.yml`
- `backend/src/test/java/com/example/railway/RailwayBookingApplicationTests.java`

### What To Learn

`pom.xml` is the Maven project file. It defines the project name, Java version, dependencies, and build plugins.

`RailwayBookingApplication.java` is the Spring Boot startup class. Running this class starts the backend server.

`application.yml` stores backend configuration, such as the server port and application name.

`HealthController.java` defines the first API endpoint:

```text
GET /api/health
```

This endpoint is used to check whether the backend service is running.

`ApiResponse.java` defines a unified response format. In real projects, backend APIs usually return data in a consistent structure.

Example:

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

`GlobalExceptionHandler.java` catches backend exceptions and returns a consistent error response.

### Git Reminder

After checking the files in Cursor, commit this stage:

```bash
git status
git add .
git commit -m "create spring boot backend skeleton"
```

### Next Step

Run the backend locally and call the health check API:

```bash
cd backend
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080/api/health
```

## 2026-06-16

### 今天做了什么

进入第二阶段前，先补充数据库设计文档：

- `docs/database-design.md`

这一步不是直接写登录代码，而是先把系统里要保存的数据想清楚。铁路订票系统后面会有用户、角色、车站、车次、余票、订单、支付等模块，这些模块最终都需要落到数据库表里。

### 今天要理解什么

后端项目可以先按这条线理解：

```text
浏览器请求
  -> Controller 接请求
  -> Service 处理业务规则
  -> Mapper/DAO 查数据库
  -> MySQL 保存数据
```

你之前做过的学生选课系统里已经有类似结构：

- `Controller`：接收页面请求，例如登录、查看课程。
- `DAO`：写 SQL，连接数据库。
- `Entity`：表示数据库里的一行数据，例如 `Student`、`Course`。

现在这个铁路项目会用更规范的写法重新做一遍。

### 下一步

开始做用户模块的最小版本：

1. 添加用户相关依赖和配置。
2. 创建 `User` 实体。
3. 创建注册接口。
4. 创建登录接口。
5. 后续再加入 JWT 和权限控制。

### 本次新增：用户注册模块第一版

新增后端依赖：

- MyBatis-Plus：替代旧项目里手写 JDBC/DAO 的方式，用 Mapper 操作数据库。
- MySQL Driver：让 Java 程序能连接 MySQL。
- Spring Security Crypto：只使用其中的 BCrypt 密码加密工具，暂时还没有启用完整权限控制。

新增代码：

- `user/User.java`：用户表对应的 Java 实体。
- `user/UserMapper.java`：操作 `users` 表的 Mapper。
- `user/RegisterRequest.java`：注册接口接收的请求参数。
- `user/UserResponse.java`：注册成功后返回给前端的数据。
- `user/UserService.java`：注册业务逻辑，包括用户名查重和密码加密。
- `user/UserController.java`：注册接口入口。
- `schema.sql`：第一张表 `users` 的建表 SQL。

新增接口：

```text
POST /api/users/register
```

请求示例：

```json
{
  "username": "testuser",
  "password": "123456",
  "phone": "13800000000",
  "email": "test@example.com"
}
```

这一版还没有 JWT，也没有登录接口。现在只是先把“注册用户并保存到数据库”这条链路搭出来。

### 本次验证：注册接口真实写入数据库

已经确认：

- 本机 MySQL 服务 `MySQL80` 正在运行。
- 正确的本地数据库账号是 `root`，密码通过环境变量 `DB_PASSWORD` 提供。
- 已创建数据库 `railway_booking`。
- 已创建数据表 `users`。
- `POST /api/users/register` 已经成功注册测试用户。

为了方便在 IDEA 里测试接口，新增：

- `backend/api-tests.http`

这个文件里包含：

- `GET /api/health`
- `POST /api/users/register`

注意：浏览器地址栏默认发的是 GET 请求，所以不能直接用地址栏测试注册接口。注册接口必须用 POST，并且带 JSON 请求体。

### 本次新增：用户登录接口第一版

新增代码：

- `user/LoginRequest.java`：登录接口接收的请求参数。
- `user/LoginResponse.java`：登录成功后返回给前端的数据。

更新代码：

- `UserService`：新增 `login` 方法。
- `UserController`：新增 `POST /api/users/login` 接口。
- `api-tests.http`：新增登录接口测试请求。

登录流程：

```text
前端提交 username 和 password
  -> UserController 接收请求
  -> UserService 根据 username 查询 users 表
  -> 使用 BCrypt 校验密码是否匹配
  -> 校验用户状态是否 ENABLED
  -> 返回登录成功的用户信息
```

注意：这一版登录成功后还没有返回 JWT。现在只是先验证“用户名 + 密码是否正确”。下一步会加入 JWT，让后端能够识别“这个请求是谁发来的”。

### 本次新增：JWT 登录通行证第一版

新增依赖：

- JJWT：用于生成和解析 JWT token。

新增代码：

- `common/UnauthorizedException.java`：表示用户没有登录或 token 无效。
- `user/JwtService.java`：负责生成 token、解析 token。

更新代码：

- `LoginResponse`：登录成功后返回 token。
- `UserService`：登录成功后生成 token，并新增当前用户查询逻辑。
- `UserController`：新增 `GET /api/users/me`。
- `GlobalExceptionHandler`：把未登录或 token 无效统一返回 401。
- `api-tests.http`：新增当前用户接口测试。

现在用户模块的流程是：

```text
注册
  -> 保存用户

登录
  -> 校验用户名和密码
  -> 返回 JWT token

查询当前用户
  -> 请求头带 Authorization: Bearer token
  -> 后端解析 token
  -> 返回当前登录用户信息
```

可以先把 JWT 理解成“登录成功后发给用户的一张临时通行证”。后续查订单、下单等需要登录的接口，都会要求用户带上这张通行证。

### 本次新增：基础业务日志

新增日志位置：

- `UserService`：记录用户注册成功、登录成功、登录失败原因。
- `GlobalExceptionHandler`：记录 400、401、405 和未处理的 500 异常。

日志原则：

- 可以记录用户 ID、用户名、业务结果。
- 不记录明文密码。
- 不记录 JWT token。
- 500 异常需要记录完整异常堆栈，方便排查代码问题。

这一步让项目更接近企业后端。真实项目上线后，排查问题通常不是靠猜，而是看日志。

### 阶段交接文档

用户模块阶段已经整理成独立总结：

- `docs/stage-01-user-module-summary.md`

如果切换到新的 Codex 窗口，可以让新窗口先阅读这份文档，再继续车站模块。

### 本次新增：车站模块第一版

新增数据库表：

- `stations`：保存车站名称、所在城市、车站编码。

新增代码：

- `station/Station.java`：车站表对应的 Java 实体。
- `station/StationMapper.java`：操作 `stations` 表的 Mapper。
- `station/StationRequest.java`：新增和修改车站时接收的请求参数。
- `station/StationResponse.java`：返回给前端的车站数据。
- `station/StationService.java`：车站业务逻辑，包括列表查询、查重、新增、修改、删除。
- `station/StationController.java`：车站接口入口。

新增接口：

```text
GET /api/stations
GET /api/stations?keyword=北京
POST /api/admin/stations
PUT /api/admin/stations/{id}
DELETE /api/admin/stations/{id}
```

这一版先用 `/api/admin/stations` 表示管理接口，但还没有真正接入管理员权限校验。后面加入 Spring Security 和角色权限后，再让这些接口只允许管理员访问。

车站模块可以按这条链路理解：

```text
接口测试工具
  -> StationController 接收请求
  -> StationService 判断车站名和编码是否重复
  -> StationMapper 操作 MySQL
  -> stations 表保存或查询车站
  -> ApiResponse 返回统一 JSON
```

已经验证：

- `stations` 表已创建到本地 `railway_booking` 数据库。
- `mvn test` 通过，结果为 `BUILD SUCCESS`。

### 本次新增：车次和经停车站模块第一版

新增数据库表：

- `trains`：保存车次基本信息，例如车次号、车次类型、状态。
- `train_stations`：保存一趟车经过哪些站，以及每站的到达时间和发车时间。

新增代码：

- `train/Train.java`：车次表对应的 Java 实体。
- `train/TrainStation.java`：经停车站表对应的 Java 实体。
- `train/TrainMapper.java`：操作 `trains` 表的 Mapper。
- `train/TrainStationMapper.java`：操作 `train_stations` 表的 Mapper。
- `train/TrainRequest.java`：新增和修改车次时接收的请求参数。
- `train/TrainResponse.java`：返回给前端的车次数据。
- `train/TrainStationRequest.java`：设置经停车站时接收的请求参数。
- `train/TrainStationResponse.java`：返回给前端的经停车站数据。
- `train/TrainService.java`：车次和经停车站业务逻辑。
- `train/TrainController.java`：车次接口入口。

新增接口：

```text
GET /api/trains
GET /api/trains?keyword=G
POST /api/admin/trains
PUT /api/admin/trains/{id}
DELETE /api/admin/trains/{id}
GET /api/trains/{id}/stations
PUT /api/admin/trains/{id}/stations
```

这一版经停车站使用“整条线路一次提交并替换”的方式。比如设置 G101 的路线时，一次提交北京南、济南西、南京南、上海虹桥这些站，而不是一个站一个站单独修改。

车次模块可以按这条链路理解：

```text
接口测试工具
  -> TrainController 接收请求
  -> TrainService 判断车次号是否重复、站点是否存在、站序是否重复
  -> TrainMapper / TrainStationMapper 操作 MySQL
  -> trains / train_stations 表保存车次和路线
  -> ApiResponse 返回统一 JSON
```

已经验证：

- `trains` 和 `train_stations` 表已创建到本地 `railway_booking` 数据库。
- `mvn test` 通过，结果为 `BUILD SUCCESS`。

### 本次新增：查票模块第一版

新增数据库表：

- `train_daily_schedules`：保存某一天某趟车是否开行。
- `ticket_inventory`：保存某天某趟车、某个出发站到到达站、某个座位类型的余票和票价。

新增代码：

- `ticket/TrainDailySchedule.java`：每日车次表对应的 Java 实体。
- `ticket/TicketInventory.java`：余票库存表对应的 Java 实体。
- `ticket/TrainDailyScheduleMapper.java`：操作 `train_daily_schedules` 表的 Mapper。
- `ticket/TicketInventoryMapper.java`：操作 `ticket_inventory` 表的 Mapper。
- `ticket/ScheduleRequest.java`：创建每日车次时接收的请求参数。
- `ticket/ScheduleResponse.java`：返回每日车次数据。
- `ticket/InventoryRequest.java`：创建库存时接收的请求参数。
- `ticket/InventoryResponse.java`：返回库存数据。
- `ticket/TicketSearchResponse.java`：查票接口返回给前端的数据。
- `ticket/TicketService.java`：查票业务逻辑。
- `ticket/TicketController.java`：查票接口入口。

新增接口：

```text
POST /api/admin/schedules
POST /api/admin/inventory
GET /api/tickets/search?departureStationId=1&arrivalStationId=2&travelDate=2026-06-20
GET /api/tickets/search?departureStationId=1&arrivalStationId=2&travelDate=2026-06-20&seatType=SECOND_CLASS
```

查票模块可以按这条链路理解：

```text
用户输入出发站、到达站、日期
  -> TicketController 接收请求
  -> TicketService 查询库存
  -> 检查每日车次是否 OPEN、车次是否 ACTIVE
  -> 检查出发站是否在到达站前面
  -> 返回车次号、时间、余票、票价
```

本地数据库已添加一组演示数据：

```text
北京南 -> 上海虹桥
G101
2026-06-20
SECOND_CLASS
余票 100
票价 553.00
```

已经验证：

- `train_daily_schedules` 和 `ticket_inventory` 表已创建到本地 `railway_booking` 数据库。
- `mvn test` 通过，结果为 `BUILD SUCCESS`。

### 本次新增：订单模块第一版

新增数据库表：

- `ticket_orders`：保存订单主信息，例如订单号、用户、订单状态、总金额。
- `ticket_order_items`：保存订单明细，例如乘车人、区间、座位类型、票价。

新增代码：

- `order/TicketOrder.java`：订单主表对应的 Java 实体。
- `order/TicketOrderItem.java`：订单明细表对应的 Java 实体。
- `order/TicketOrderMapper.java`：操作 `ticket_orders` 表的 Mapper。
- `order/TicketOrderItemMapper.java`：操作 `ticket_order_items` 表的 Mapper。
- `order/CreateOrderRequest.java`：创建订单时接收的请求参数。
- `order/OrderItemResponse.java`：返回订单明细数据。
- `order/OrderResponse.java`：返回订单主信息和明细。
- `order/OrderService.java`：订单业务逻辑。
- `order/OrderController.java`：订单接口入口。

新增接口：

```text
POST /api/orders
GET /api/orders
GET /api/orders/{id}
```

第一版下单流程：

```text
用户带 JWT token 请求下单
  -> 后端解析当前 userId
  -> 查询 ticket_inventory 库存
  -> 如果有余票，available_count 减 1，locked_count 加 1
  -> 创建 ticket_orders 主订单
  -> 创建 ticket_order_items 订单明细
  -> 订单状态为 PENDING_PAYMENT
```

这一版先把主流程跑通，还没有做支付、取消、超时释放库存，也还没有 Redis 防超卖。

已经验证：

- `ticket_orders` 和 `ticket_order_items` 表已创建到本地 `railway_booking` 数据库。
- `mvn test` 通过，结果为 `BUILD SUCCESS`。

### 本次新增：模拟支付模块第一版

新增数据库表：

- `payments`：保存模拟支付流水，例如支付流水号、订单 ID、支付金额、支付状态、支付时间。

新增代码：

- `payment/Payment.java`：支付表对应的 Java 实体。
- `payment/PaymentMapper.java`：操作 `payments` 表的 Mapper。
- `payment/PaymentResponse.java`：返回给前端的支付数据。
- `payment/PaymentService.java`：支付业务逻辑。
- `payment/PaymentController.java`：支付接口入口。

新增接口：

```text
POST /api/orders/{id}/pay
```

第一版支付流程：

```text
用户带 JWT token 请求支付订单
  -> 后端确认订单属于当前用户
  -> 确认订单状态是 PENDING_PAYMENT
  -> 创建 payments 支付流水，状态为 SUCCESS
  -> 把 ticket_orders 订单状态改为 PAID
  -> 把 locked_count 减 1
```

这里的库存可以这样理解：

```text
下单时：available_count 减 1，locked_count 加 1
支付时：available_count 不变，locked_count 减 1
```

也就是说，下单已经把票从可售库存里拿走；支付成功后，这张票不再是“锁定中”，而是已经正式售出。

已经验证：

- `payments` 表已创建到本地 `railway_booking` 数据库。
- `mvn test` 通过，结果为 `BUILD SUCCESS`。

### 本次新增：取消订单模块第一版

更新代码：

- `order/OrderService.java`：新增取消订单业务逻辑。
- `order/OrderController.java`：新增取消订单接口。
- `api-tests.http`：新增取消订单接口测试请求。

新增接口：

```text
POST /api/orders/{id}/cancel
```

第一版取消流程：

```text
用户带 JWT token 请求取消订单
  -> 后端确认订单属于当前用户
  -> 确认订单状态是 PENDING_PAYMENT
  -> 把 locked_count 减 1
  -> 把 available_count 加 1
  -> 把 ticket_orders 订单状态改为 CANCELLED
  -> 写入 cancelled_at
```

取消订单可以理解成“把未支付订单占住的票还回去”。已经支付的订单不能用这个接口取消，后面如果要做退款，会单独设计退款流程。

已经验证：

- `mvn test` 通过，结果为 `BUILD SUCCESS`。

### 本次新增：未支付订单超时自动取消

更新代码：

- `RailwayBookingApplication.java`：启用 Spring 定时任务。
- `order/OrderService.java`：新增定时扫描并取消超时未支付订单的逻辑。
- `application.yml`：新增订单超时配置。

新增配置：

```yaml
app:
  order:
    payment-timeout-minutes: 15
    timeout-initial-delay-ms: 60000
    timeout-scan-ms: 60000
```

第一版超时取消流程：

```text
系统启动 60 秒后开始第一次扫描
  -> 之后每 60 秒扫描一次
  -> 找出创建时间超过 15 分钟、状态仍为 PENDING_PAYMENT 的订单
  -> 把 locked_count 减 1
  -> 把 available_count 加 1
  -> 把订单状态改为 CANCELLED
  -> 写入 cancelled_at
```

可以把这个功能理解成“后台值班员”。用户不需要手动操作，系统会定期检查过期未支付订单，并把它们占住的票还回库存。

已经验证：

- `mvn test` 通过，结果为 `BUILD SUCCESS`。
