CREATE TABLE IF NOT EXISTS users (
    id          UUID         PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS stations (
    code VARCHAR(50)  PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS trips (
    id                      UUID             PRIMARY KEY,
    departure_station_code  VARCHAR(50)      NOT NULL,
    arrival_station_code    VARCHAR(50)      NOT NULL,
    departure_time          TIMESTAMP        NOT NULL,
    arrival_time            TIMESTAMP        NOT NULL,
    price                   DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS tickets (
    id                      UUID             PRIMARY KEY,
    holder_id               UUID             NOT NULL,
    valid_from              TIMESTAMP        NOT NULL,
    valid_until             TIMESTAMP        NOT NULL,
    departure_station_code  VARCHAR(50)      NOT NULL,
    arrival_station_code    VARCHAR(50)      NOT NULL,
    departure_time          TIMESTAMP        NOT NULL,
    arrival_time            TIMESTAMP        NOT NULL,
    price                   DOUBLE PRECISION NOT NULL,
    status                  VARCHAR(50)      NOT NULL,
    issued_at               TIMESTAMP        NOT NULL
);

CREATE TABLE IF NOT EXISTS validation_records (
    id           UUID      PRIMARY KEY,
    ticket_id    UUID      NOT NULL,
    agent_id     UUID      NOT NULL,
    validated_at TIMESTAMP NOT NULL,
    result       VARCHAR(50) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_tickets_holder   ON tickets(holder_id);
CREATE INDEX IF NOT EXISTS idx_tickets_status   ON tickets(status);
CREATE INDEX IF NOT EXISTS idx_trips_route      ON trips(departure_station_code, arrival_station_code, departure_time);
CREATE INDEX IF NOT EXISTS idx_validation_ticket ON validation_records(ticket_id);
