SET NAMES utf8mb4;

INSERT INTO users (username, password_hash, phone, email, role, status, created_at, updated_at)
VALUES (
    'admin',
    '$2a$10$NLjXvjsiqNWdoIdBNHOuJuvnF2sM5jn7vuY0n5tNvxhkQId4VM5Na',
    '13800000001',
    'admin@example.com',
    'ADMIN',
    'ENABLED',
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    role = 'ADMIN',
    status = 'ENABLED',
    updated_at = NOW();

INSERT IGNORE INTO stations (name, city, code, created_at, updated_at)
VALUES
    ('北京南', '北京', 'BJN', NOW(), NOW()),
    ('天津西', '天津', 'TJX', NOW(), NOW()),
    ('济南西', '济南', 'JNX', NOW(), NOW()),
    ('南京南', '南京', 'NJN', NOW(), NOW()),
    ('上海虹桥', '上海', 'SHH', NOW(), NOW()),
    ('杭州东', '杭州', 'HZD', NOW(), NOW()),
    ('郑州东', '郑州', 'ZZD', NOW(), NOW()),
    ('武汉', '武汉', 'WHN', NOW(), NOW()),
    ('长沙南', '长沙', 'CSN', NOW(), NOW()),
    ('广州南', '广州', 'GZN', NOW(), NOW()),
    ('深圳北', '深圳', 'SZB', NOW(), NOW()),
    ('西安北', '西安', 'XAB', NOW(), NOW());

INSERT IGNORE INTO trains (train_no, train_type, status, created_at, updated_at)
VALUES
    ('G101', 'G', 'ACTIVE', NOW(), NOW()),
    ('G103', 'G', 'ACTIVE', NOW(), NOW()),
    ('D901', 'D', 'ACTIVE', NOW(), NOW()),
    ('G300', 'G', 'ACTIVE', NOW(), NOW()),
    ('G650', 'G', 'ACTIVE', NOW(), NOW());

INSERT IGNORE INTO train_stations (train_id, station_id, stop_order, arrival_time, departure_time)
SELECT t.id, s.id, route.stop_order, route.arrival_time, route.departure_time
FROM trains t
JOIN (
    SELECT 'G101' AS train_no, 'BJN' AS station_code, 1 AS stop_order, NULL AS arrival_time, '08:00:00' AS departure_time
    UNION ALL SELECT 'G101', 'JNX', 2, '09:30:00', '09:35:00'
    UNION ALL SELECT 'G101', 'NJN', 3, '11:20:00', '11:25:00'
    UNION ALL SELECT 'G101', 'SHH', 4, '12:30:00', NULL

    UNION ALL SELECT 'G103', 'BJN', 1, NULL, '09:10:00'
    UNION ALL SELECT 'G103', 'TJX', 2, '09:45:00', '09:48:00'
    UNION ALL SELECT 'G103', 'JNX', 3, '10:55:00', '11:00:00'
    UNION ALL SELECT 'G103', 'NJN', 4, '12:45:00', '12:50:00'
    UNION ALL SELECT 'G103', 'HZD', 5, '13:50:00', '13:55:00'
    UNION ALL SELECT 'G103', 'SHH', 6, '14:45:00', NULL

    UNION ALL SELECT 'D901', 'SHH', 1, NULL, '07:20:00'
    UNION ALL SELECT 'D901', 'HZD', 2, '08:15:00', '08:20:00'
    UNION ALL SELECT 'D901', 'NJN', 3, '10:10:00', '10:15:00'
    UNION ALL SELECT 'D901', 'BJN', 4, '15:30:00', NULL

    UNION ALL SELECT 'G300', 'GZN', 1, NULL, '08:30:00'
    UNION ALL SELECT 'G300', 'CSN', 2, '10:55:00', '11:00:00'
    UNION ALL SELECT 'G300', 'WHN', 3, '12:20:00', '12:25:00'
    UNION ALL SELECT 'G300', 'ZZD', 4, '14:30:00', '14:35:00'
    UNION ALL SELECT 'G300', 'BJN', 5, '17:50:00', NULL

    UNION ALL SELECT 'G650', 'XAB', 1, NULL, '08:05:00'
    UNION ALL SELECT 'G650', 'ZZD', 2, '10:05:00', '10:10:00'
    UNION ALL SELECT 'G650', 'WHN', 3, '12:25:00', '12:30:00'
    UNION ALL SELECT 'G650', 'CSN', 4, '14:00:00', '14:05:00'
    UNION ALL SELECT 'G650', 'GZN', 5, '16:25:00', '16:30:00'
    UNION ALL SELECT 'G650', 'SZB', 6, '17:05:00', NULL
) route ON route.train_no = t.train_no
JOIN stations s ON s.code = route.station_code;

INSERT IGNORE INTO train_daily_schedules (train_id, travel_date, status, created_at, updated_at)
SELECT t.id, dates.travel_date, 'OPEN', NOW(), NOW()
FROM trains t
JOIN (
    SELECT DATE '2026-06-20' AS travel_date
    UNION ALL SELECT DATE '2026-06-21'
    UNION ALL SELECT DATE '2026-06-22'
    UNION ALL SELECT DATE '2026-06-23'
    UNION ALL SELECT DATE '2026-06-24'
) dates
WHERE t.train_no IN ('G101', 'G103', 'D901', 'G300', 'G650');

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
SELECT sch.id, dep.id, arr.id, seed.seat_type, seed.total_count, seed.available_count, 0, seed.price
FROM (
    SELECT 'G101' AS train_no, 'BJN' AS dep_code, 'SHH' AS arr_code, 'SECOND_CLASS' AS seat_type, 120 AS total_count, 120 AS available_count, 553.00 AS price
    UNION ALL SELECT 'G101', 'BJN', 'SHH', 'FIRST_CLASS', 48, 48, 933.00
    UNION ALL SELECT 'G101', 'BJN', 'NJN', 'SECOND_CLASS', 80, 80, 443.00
    UNION ALL SELECT 'G101', 'JNX', 'SHH', 'SECOND_CLASS', 70, 70, 398.00

    UNION ALL SELECT 'G103', 'BJN', 'SHH', 'SECOND_CLASS', 110, 110, 558.00
    UNION ALL SELECT 'G103', 'BJN', 'HZD', 'SECOND_CLASS', 90, 90, 506.00
    UNION ALL SELECT 'G103', 'TJX', 'SHH', 'SECOND_CLASS', 75, 75, 515.00
    UNION ALL SELECT 'G103', 'BJN', 'SHH', 'BUSINESS_CLASS', 12, 12, 1748.00

    UNION ALL SELECT 'D901', 'SHH', 'BJN', 'SECOND_CLASS', 140, 140, 408.00
    UNION ALL SELECT 'D901', 'HZD', 'BJN', 'SECOND_CLASS', 90, 90, 378.00
    UNION ALL SELECT 'D901', 'SHH', 'NJN', 'SECOND_CLASS', 100, 100, 158.00

    UNION ALL SELECT 'G300', 'GZN', 'BJN', 'SECOND_CLASS', 120, 120, 862.00
    UNION ALL SELECT 'G300', 'GZN', 'BJN', 'FIRST_CLASS', 42, 42, 1380.00
    UNION ALL SELECT 'G300', 'CSN', 'BJN', 'SECOND_CLASS', 85, 85, 649.00
    UNION ALL SELECT 'G300', 'WHN', 'BJN', 'SECOND_CLASS', 70, 70, 520.00

    UNION ALL SELECT 'G650', 'XAB', 'SZB', 'SECOND_CLASS', 100, 100, 888.00
    UNION ALL SELECT 'G650', 'XAB', 'GZN', 'SECOND_CLASS', 95, 95, 813.00
    UNION ALL SELECT 'G650', 'ZZD', 'SZB', 'SECOND_CLASS', 80, 80, 735.00
    UNION ALL SELECT 'G650', 'WHN', 'GZN', 'SECOND_CLASS', 75, 75, 463.00
) seed
JOIN trains t ON t.train_no = seed.train_no
JOIN train_daily_schedules sch ON sch.train_id = t.id
JOIN stations dep ON dep.code = seed.dep_code
JOIN stations arr ON arr.code = seed.arr_code
WHERE sch.travel_date BETWEEN DATE '2026-06-20' AND DATE '2026-06-24';
