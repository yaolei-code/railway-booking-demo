CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS stations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    city VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS trains (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_no VARCHAR(20) NOT NULL UNIQUE,
    train_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS train_stations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    stop_order INT NOT NULL,
    arrival_time TIME,
    departure_time TIME,
    CONSTRAINT uk_train_stations_train_order UNIQUE (train_id, stop_order),
    CONSTRAINT uk_train_stations_train_station UNIQUE (train_id, station_id),
    CONSTRAINT fk_train_stations_train FOREIGN KEY (train_id) REFERENCES trains(id),
    CONSTRAINT fk_train_stations_station FOREIGN KEY (station_id) REFERENCES stations(id)
);

CREATE TABLE IF NOT EXISTS train_daily_schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_id BIGINT NOT NULL,
    travel_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT uk_train_daily_schedules_train_date UNIQUE (train_id, travel_date),
    CONSTRAINT fk_train_daily_schedules_train FOREIGN KEY (train_id) REFERENCES trains(id)
);

CREATE TABLE IF NOT EXISTS ticket_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    schedule_id BIGINT NOT NULL,
    departure_station_id BIGINT NOT NULL,
    arrival_station_id BIGINT NOT NULL,
    seat_type VARCHAR(30) NOT NULL,
    total_count INT NOT NULL,
    available_count INT NOT NULL,
    locked_count INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT uk_ticket_inventory_route_seat UNIQUE (
        schedule_id,
        departure_station_id,
        arrival_station_id,
        seat_type
    ),
    CONSTRAINT fk_ticket_inventory_schedule FOREIGN KEY (schedule_id) REFERENCES train_daily_schedules(id),
    CONSTRAINT fk_ticket_inventory_departure_station FOREIGN KEY (departure_station_id) REFERENCES stations(id),
    CONSTRAINT fk_ticket_inventory_arrival_station FOREIGN KEY (arrival_station_id) REFERENCES stations(id)
);

CREATE TABLE IF NOT EXISTS ticket_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(40) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    paid_at DATETIME,
    cancelled_at DATETIME,
    INDEX idx_ticket_orders_status_created_at (status, created_at),
    CONSTRAINT fk_ticket_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS ticket_order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    passenger_name VARCHAR(50) NOT NULL,
    passenger_id_no VARCHAR(30) NOT NULL,
    schedule_id BIGINT NOT NULL,
    departure_station_id BIGINT NOT NULL,
    arrival_station_id BIGINT NOT NULL,
    seat_type VARCHAR(30) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_ticket_order_items_order FOREIGN KEY (order_id) REFERENCES ticket_orders(id),
    CONSTRAINT fk_ticket_order_items_schedule FOREIGN KEY (schedule_id) REFERENCES train_daily_schedules(id),
    CONSTRAINT fk_ticket_order_items_departure_station FOREIGN KEY (departure_station_id) REFERENCES stations(id),
    CONSTRAINT fk_ticket_order_items_arrival_station FOREIGN KEY (arrival_station_id) REFERENCES stations(id)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    payment_no VARCHAR(40) NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    paid_at DATETIME,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES ticket_orders(id)
);
