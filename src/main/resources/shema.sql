CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

CREATE TABLE carriers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL
);

CREATE TABLE routes (
    id SERIAL PRIMARY KEY,
    departure VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    carrier_id INT NOT NULL,
    duration_minutes INT NOT NULL,
    CONSTRAINT fk_routes_carrier FOREIGN KEY (carrier_id)
        REFERENCES carriers (id) ON DELETE CASCADE
);

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    route_id INT NOT NULL,               -- внешний ключ на маршрут
    user_id INT,                          -- кто купил билет (NULL, если билет ещё свободен)
    date_time TIMESTAMP NOT NULL,         -- дата и время отправления
    seat_number INT NOT NULL,             -- номер места
    price NUMERIC(10, 2) NOT NULL,        -- цена
    is_sold BOOLEAN DEFAULT false,        -- продан или нет
    CONSTRAINT fk_tickets_route FOREIGN KEY (route_id)
        REFERENCES routes (id) ON DELETE CASCADE,
    CONSTRAINT fk_tickets_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE SET NULL
);