CREATE TABLE example (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

INSERT INTO example (name) VALUES
    ('Beispieldaten 1'),
    ('Beispieldaten 2');