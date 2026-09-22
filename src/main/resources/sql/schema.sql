-- schema.sql
-- Creazione delle tabelle con vincoli e foreign key

CREATE TABLE IF NOT EXISTS member (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    surname         VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    password_hash   TEXT NOT NULL,
    city            VARCHAR(100),
    birthday        DATE,
    has_license     BOOLEAN DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS boat (
    id              SERIAL PRIMARY KEY,
    reg_num         VARCHAR(50)  NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(20)  NOT NULL,
    seats           INTEGER      NOT NULL CHECK (seats > 0),
    photo_url       TEXT,
    description     TEXT
    );

CREATE TABLE IF NOT EXISTS skipper (
    id              SERIAL PRIMARY KEY,
    member_id       INTEGER NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    boat_id         INTEGER REFERENCES boat(id) ON DELETE SET NULL,
    certificate     VARCHAR(100),
    avg_rating      NUMERIC(3,2) DEFAULT 0.0,
    bio             TEXT
    );

CREATE TABLE IF NOT EXISTS rental (
    id               SERIAL PRIMARY KEY,
    member_id        INTEGER      NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    boat_id          INTEGER      NOT NULL REFERENCES boat(id) ON DELETE CASCADE,
    start_date       DATE         NOT NULL,
    end_date         DATE         NOT NULL,
    num_participants INTEGER      DEFAULT 0 CHECK (num_participants >= 0),
    total_price      NUMERIC(10,2) NOT NULL CHECK (total_price >= 0),
    CHECK (end_date > start_date)
    );

CREATE TABLE IF NOT EXISTS booking (
    id              SERIAL PRIMARY KEY,
    member_id       INTEGER       NOT NULL REFERENCES member(id) ON DELETE CASCADE,
    boat_id         INTEGER       NOT NULL REFERENCES boat(id) ON DELETE CASCADE,
    skipper_id      INTEGER       REFERENCES skipper(id) ON DELETE SET NULL,
    date            DATE          NOT NULL,
    seats_booked    INTEGER       NOT NULL CHECK (seats_booked > 0),
    total_price     NUMERIC(10,2) NOT NULL CHECK (total_price >= 0),
    reg_type        VARCHAR(20)   NOT NULL
    );

CREATE TABLE IF NOT EXISTS registration (
    id              SERIAL PRIMARY KEY,
    member_id       INTEGER NOT NULL UNIQUE REFERENCES member(id) ON DELETE CASCADE,
    type            VARCHAR(20) NOT NULL,
    year            INTEGER     NOT NULL CHECK (year >= 2000)
    );