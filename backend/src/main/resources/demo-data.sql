INSERT IGNORE INTO stations (name, city, code, created_at, updated_at)
VALUES
    ('北京南', '北京', 'BJN', NOW(), NOW()),
    ('上海虹桥', '上海', 'SHH', NOW(), NOW());

INSERT IGNORE INTO trains (train_no, train_type, status, created_at, updated_at)
VALUES
    ('G101', 'G', 'ACTIVE', NOW(), NOW());

INSERT IGNORE INTO train_stations (train_id, station_id, stop_order, arrival_time, departure_time)
SELECT t.id, s.id, 1, NULL, '08:00:00'
FROM trains t, stations s
WHERE t.train_no = 'G101'
  AND s.code = 'BJN';

INSERT IGNORE INTO train_stations (train_id, station_id, stop_order, arrival_time, departure_time)
SELECT t.id, s.id, 2, '12:30:00', NULL
FROM trains t, stations s
WHERE t.train_no = 'G101'
  AND s.code = 'SHH';

INSERT IGNORE INTO train_daily_schedules (train_id, travel_date, status, created_at, updated_at)
SELECT id, '2026-06-20', 'OPEN', NOW(), NOW()
FROM trains
WHERE train_no = 'G101';

INSERT IGNORE INTO ticket_inventory (
    schedule_id,
    departure_station_id,
    arrival_station_id,
    seat_type,
    total_count,
    available_count,
    locked_count,
    price
)
SELECT sch.id, dep.id, arr.id, 'SECOND_CLASS', 100, 100, 0, 553.00
FROM train_daily_schedules sch
JOIN trains t ON sch.train_id = t.id
JOIN stations dep ON dep.code = 'BJN'
JOIN stations arr ON arr.code = 'SHH'
WHERE t.train_no = 'G101'
  AND sch.travel_date = '2026-06-20';
