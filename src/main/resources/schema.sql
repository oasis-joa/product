CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL,
                       username VARCHAR(255),
                       status_message VARCHAR(255),
                       emergency_contact VARCHAR(255),
                       kakao_access_token VARCHAR(255)
);