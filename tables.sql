-- Copas aja ke mysql terminal

CREATE DATABASE futsal_management;
USE futsal_management;

-- Tabel users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    user_level ENUM('admin', 'user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert admin default
INSERT INTO users (username, password, email, user_level)
VALUES ('admin', SHA2('admin123', 256), 'admin@futsal.com', 'admin');

INSERT INTO users (username, password, email, user_level) 
VALUES ('user1', SHA2('user123', 256), 'user1@futsal.com', 'user');
