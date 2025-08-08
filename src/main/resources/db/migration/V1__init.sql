CREATE TABLE IF NOT EXISTS role (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS client_model (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            name VARCHAR(255),
    last_name VARCHAR(255),
    age INT,
    birth_date DATE,
    password VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS client_roles (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            client_id BIGINT NOT NULL,
                                            role_id BIGINT NOT NULL,
                                            CONSTRAINT uk_client_role UNIQUE (client_id, role_id),
    FOREIGN KEY (client_id) REFERENCES client_model(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
    );

INSERT INTO role (name) VALUES ('ADMIN'), ('USER');

INSERT INTO client_model (name, last_name, age, birth_date, password)
VALUES ('Juan', 'Pérez', 35, '1990-01-01', 'password');

INSERT INTO client_roles (client_id, role_id)
SELECT c.id, r.id
FROM client_model c
         JOIN role r ON r.name IN ('ADMIN', 'USER')
WHERE c.name = 'Juan' AND c.last_name = 'Pérez';
