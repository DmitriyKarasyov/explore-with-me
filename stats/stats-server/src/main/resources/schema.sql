DROP TABLE IF EXISTS endpoint_hits;

CREATE TABLE IF NOT EXISTS endpoint_hits (
    id INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL,
    app VARCHAR(60),
    uri VARCHAR(60),
    ip VARCHAR(20),
    creation_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_endpoint_hit PRIMARY KEY (id)
);