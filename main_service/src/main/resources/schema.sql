DROP TABLE IF EXISTS users, categories, locations, events, participation_requests, compilations,
compilations_events;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT unique_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
    id INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_name UNIQUE (category_name)
);

CREATE TABLE IF NOT EXISTS locations (
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    CONSTRAINT pk_locations PRIMARY KEY (lat, lon)
);

CREATE TABLE IF NOT EXISTS events (
    id INTEGER GENERATED ALWAYS AS IDENTITY,
    annotation VARCHAR(2000) NOT NULL,
    category_id INTEGER REFERENCES categories (id) ON DELETE RESTRICT,
    confirmed_requests INTEGER,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    participant_limit INTEGER DEFAULT 0,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT TRUE,
    state VARCHAR,
    title VARCHAR(120) NOT NULL,
    views INTEGER,
    CONSTRAINT pk_events PRIMARY KEY (id),
    FOREIGN KEY (lat, lon) REFERENCES locations (lat, lon)
);

CREATE TABLE IF NOT EXISTS participation_requests (
    id INTEGER GENERATED ALWAYS AS IDENTITY,
    created TIMESTAMP WITHOUT TIME ZONE,
    event_id INTEGER REFERENCES events (id) ON DELETE CASCADE,
    requester_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    status VARCHAR,
    CONSTRAINT participation_requests_pk PRIMARY KEY (id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id INTEGER GENERATED ALWAYS AS IDENTITY,
    pinned BOOLEAN,
    title VARCHAR(50),
    CONSTRAINT compilations_pk PRIMARY KEY (id),
    CONSTRAINT uq_compilation_title UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS compilations_events (
    id INTEGER NOT NULL,
    event_id INTEGER NOT NULL
);