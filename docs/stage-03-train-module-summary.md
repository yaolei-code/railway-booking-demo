# 阶段总结：车次模块

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

车次模块用于保存“有哪些车”和“一趟车经过哪些站”。

前面已经有：

```text
用户模块：系统知道谁在使用
车站模块：系统知道有哪些车站
```

现在新增：

```text
车次模块：系统知道有哪些车，以及每趟车的路线
```

## 2. 本阶段新增表

```text
trains
train_stations
```

`trains` 保存车次基本信息，例如：

```text
G101，高铁，ACTIVE
```

`train_stations` 保存一趟车的经停路线，例如：

```text
G101 第 1 站 北京南 08:00 发车
G101 第 2 站 济南西 09:30 到达，09:35 发车
G101 第 3 站 上海虹桥 12:30 到达
```

## 3. 本阶段新增代码

新增后端包：

```text
backend/src/main/java/com/example/railway/train
```

核心文件：

```text
Train.java
TrainStation.java
TrainMapper.java
TrainStationMapper.java
TrainRequest.java
TrainResponse.java
TrainStationRequest.java
TrainStationResponse.java
TrainService.java
TrainController.java
```

## 4. 当前接口

车次接口：

```text
GET /api/trains
GET /api/trains?keyword=G
POST /api/admin/trains
PUT /api/admin/trains/{id}
DELETE /api/admin/trains/{id}
```

经停车站接口：

```text
GET /api/trains/{id}/stations
PUT /api/admin/trains/{id}/stations
```

注意：当前 `/api/admin/...` 只是管理接口路径命名，还没有接入真正的管理员权限校验。

## 5. 请求示例

新增车次：

```json
{
  "trainNo": "G101",
  "trainType": "G",
  "status": "ACTIVE"
}
```

设置经停车站：

```json
[
  {
    "stationId": 1,
    "stopOrder": 1,
    "arrivalTime": null,
    "departureTime": "08:00:00"
  },
  {
    "stationId": 2,
    "stopOrder": 2,
    "arrivalTime": "12:30:00",
    "departureTime": null
  }
]
```

## 6. 当前业务规则

新增或修改车次时：

- 车次号不能为空。
- 车次类型不能为空。
- 状态不能为空。
- 车次号不能重复。

设置经停车站时：

- 一趟车至少要有两个站。
- 站序 `stopOrder` 不能重复。
- 同一趟车不能重复经过同一个车站。
- 每个 `stationId` 必须真实存在于 `stations` 表。

删除车次时：

- 先删除这趟车的经停车站。
- 再删除车次本身。

## 7. 给初学者的理解方式

可以把 `trains` 理解成“车次名单”：

```text
G101
G102
D203
```

可以把 `train_stations` 理解成“每趟车的路线表”：

```text
G101 -> 北京南 -> 济南西 -> 上海虹桥
```

所以后面做查票时，系统才能判断：

```text
这趟车是否经过出发站？
这趟车是否经过到达站？
出发站是否在到达站前面？
```

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

本地 MySQL 数据库 `railway_booking` 中也已创建 `trains` 和 `train_stations` 表。

## 9. 下一阶段建议

下一步可以开发：

```text
查票模块
```

推荐顺序：

1. 创建每日车次表 `train_daily_schedules`。
2. 创建余票库存表 `ticket_inventory`。
3. 实现按出发站、到达站、日期查询车票。
4. 返回车次号、出发时间、到达时间、历时、余票数量。
