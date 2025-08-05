CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(32) UNIQUE NOT NULL,
                       password VARCHAR(128) NOT NULL,
                       first_name VARCHAR(16) NOT NULL,
                       last_name VARCHAR(16) NOT NULL,
                       phone VARCHAR(20) NOT NULL CHECK (phone ~ '\+7\s?\(?\d{3}\)?\s?\d{3}-?\d{2}-?\d{2}'),
    role VARCHAR(10) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ads (
                     id SERIAL PRIMARY KEY,
                     title VARCHAR(32) NOT NULL,
                     description VARCHAR(64) NOT NULL,
                     price INTEGER NOT NULL CHECK (price >= 0 AND price <= 10000000),
                     image_path VARCHAR(255),
                     author_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          text VARCHAR(64) NOT NULL,
                          ad_id INTEGER NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
                          author_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          created_at BIGINT NOT NULL
);