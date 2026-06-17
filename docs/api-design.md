# API Design

This document records the current backend API surface for the railway booking demo.

All successful responses use the same wrapper:

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 1. Health

### GET /api/health

Checks whether the backend service is running.

## 2. Users

### POST /api/users/register

Registers a new user.

Request:

```json
{
  "username": "testuser",
  "password": "123456",
  "phone": "13800000000",
  "email": "test@example.com"
}
```

### POST /api/users/login

Logs in and returns a JWT token.

Request:

```json
{
  "username": "testuser",
  "password": "123456"
}
```

### GET /api/users/me

Returns the current logged-in user.

Header:

```text
Authorization: Bearer token
```

## 3. Stations

### GET /api/stations

Lists stations.

Optional query:

```text
keyword=北京
```

### POST /api/admin/stations

Creates a station.

Request:

```json
{
  "name": "北京南",
  "city": "北京",
  "code": "BJN"
}
```

### PUT /api/admin/stations/{id}

Updates a station.

### DELETE /api/admin/stations/{id}

Deletes a station.

## 4. Trains

### GET /api/trains

Lists trains.

Optional query:

```text
keyword=G
```

### POST /api/admin/trains

Creates a train.

Request:

```json
{
  "trainNo": "G101",
  "trainType": "G",
  "status": "ACTIVE"
}
```

### PUT /api/admin/trains/{id}

Updates a train.

### DELETE /api/admin/trains/{id}

Deletes a train and its stop stations.

### GET /api/trains/{id}/stations

Lists a train's stop stations.

### PUT /api/admin/trains/{id}/stations

Replaces a train's full stop station route.

Request:

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

## 5. Tickets

### POST /api/admin/schedules

Creates a daily train schedule.

Request:

```json
{
  "trainId": 1,
  "travelDate": "2026-06-20",
  "status": "OPEN"
}
```

### POST /api/admin/inventory

Creates ticket inventory.

Request:

```json
{
  "scheduleId": 1,
  "departureStationId": 1,
  "arrivalStationId": 2,
  "seatType": "SECOND_CLASS",
  "totalCount": 100,
  "availableCount": 100,
  "lockedCount": 0,
  "price": 553.00
}
```

### GET /api/tickets/search

Searches tickets by route and date.

Query:

```text
departureStationId=1
arrivalStationId=2
travelDate=2026-06-20
seatType=SECOND_CLASS
```

`seatType` is optional.

The response item includes `inventoryId`, which is used when creating an order.

## 6. Orders

All order APIs require:

```text
Authorization: Bearer token
```

### POST /api/orders

Creates a pending payment order.

Request:

```json
{
  "inventoryId": 1,
  "passengerName": "张三",
  "passengerIdNo": "110101199001011234"
}
```

### GET /api/orders

Lists the current user's orders.

### GET /api/orders/{id}

Returns the current user's order detail.

### POST /api/orders/{id}/pay

Simulates payment for a pending order.

### POST /api/orders/{id}/cancel

Cancels a pending payment order and releases locked inventory.

## 7. Current Demo Data

The demo data script creates multiple stations, trains, schedules, and ticket inventories.

Current seed scope:

```text
12 stations
5 trains
5 travel dates: 2026-06-20 to 2026-06-24
multiple route inventories and seat types
```

Run the script after `schema.sql`:

```bash
mysql -uroot -p050607 railway_booking -e "source backend/src/main/resources/demo-data.sql"
```
