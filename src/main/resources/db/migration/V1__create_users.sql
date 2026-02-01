CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    login VARCHAR(15) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL
);

CREATE INDEX idx_users_login ON users(login);