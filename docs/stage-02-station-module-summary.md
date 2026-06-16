# 阶段总结：车站模块

本文档用于在切换 Codex 窗口或继续下一阶段开发时快速接上上下文。

## 1. 当前阶段目标

车站模块是铁路订票系统的基础数据模块。

后面做车次、经停车站、余票查询、下单时，都需要先有车站数据。例如：

```text
北京南 -> 上海虹桥
```

这里的“北京南”和“上海虹桥”就来自 `stations` 表。

## 2. 本阶段新增内容

新增数据库表：

```text
stations
```

新增后端包：

```text
backend/src/main/java/com/example/railway/station
```

新增核心代码：

```text
Station.java
StationMapper.java
StationRequest.java
StationResponse.java
StationService.java
StationController.java
```

更新文件：

```text
backend/src/main/resources/schema.sql
backend/api-tests.http
docs/dev-log.md
```

## 3. 当前接口

公开查询接口：

```text
GET /api/stations
GET /api/stations?keyword=北京
```

管理接口：

```text
POST /api/admin/stations
PUT /api/admin/stations/{id}
DELETE /api/admin/stations/{id}
```

注意：当前 `/api/admin/stations` 只是接口路径命名，还没有接入真正的管理员权限校验。

## 4. 请求示例

新增车站：

```json
{
  "name": "北京南",
  "city": "北京",
  "code": "BJN"
}
```

字段含义：

- `name`：车站名。
- `city`：所在城市。
- `code`：车站编码，用来做系统内部识别。

## 5. 当前业务规则

新增或修改车站时：

- 车站名不能为空。
- 城市不能为空。
- 车站编码不能为空。
- 车站名不能重复。
- 车站编码不能重复。

删除车站时：

- 如果车站不存在，返回错误。
- 如果车站存在，则从 `stations` 表删除。

## 6. 给初学者的理解方式

车站模块和用户模块的代码结构很像：

```text
Controller：接收 HTTP 请求
Service：处理业务规则
Mapper：操作数据库
Entity：表示数据库一行数据
Request：前端传进来的数据
Response：后端返回给前端的数据
```

可以把 `StationService` 当成“车站管理员”：

- 新增前先检查名字和编码有没有重复。
- 修改前先确认车站存在。
- 删除前也先确认车站存在。

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

本地 MySQL 数据库 `railway_booking` 中也已创建 `stations` 表。

## 8. 下一阶段建议

下一步可以继续开发：

```text
车次模块
```

推荐顺序：

1. 创建 `trains` 表。
2. 创建 `train_stations` 表。
3. 实现车次新增和列表查询。
4. 实现车次经停车站管理。
5. 为后续查票接口准备基础数据。
