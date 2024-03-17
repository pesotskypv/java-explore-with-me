CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000),
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INTEGER NOT NULL,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state VARCHAR(20) NOT NULL,
    title VARCHAR(512) NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT fk_events_categories FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_events_initiator FOREIGN KEY (initiator_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS participation_requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    requester_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_participation_request PRIMARY KEY (id),
    CONSTRAINT fk_participation_request_on_events FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_participation_request_on_requester FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT uq_participant_per_event UNIQUE (requester_id, event_id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    title VARCHAR(50) NOT NULL,
    pinned BOOLEAN NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT pk_compilations_events PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilations_events FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT fk_events_compilations FOREIGN KEY (event_id) REFERENCES events (id)
);