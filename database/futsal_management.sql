-- Copas aja ke mysql terminal
-- atau gunakan tools phpMyAdmin langsung import file ini

CREATE DATABASE IF NOT EXISTS futsal_management;
USE futsal_management;

-- Tabel users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    user_level ENUM('admin', 'user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel futsal_fields
CREATE TABLE IF NOT EXISTS futsal_fields (
    id INT AUTO_INCREMENT PRIMARY KEY,
    field_name VARCHAR(100) NOT NULL,
    open_time TIME NOT NULL,
    close_time TIME NOT NULL,
    price_per_session DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- hapus bagian ini jika sudah pernah di run sebelumnya
TRUNCATE TABLE futsal_fields;
TRUNCATE TABLE users;

-- Insert sample data
INSERT INTO futsal_fields (field_name, open_time, close_time, price_per_session) VALUES
('Lapangan A', '08:00:00', '22:00:00', 150000.00),
('Lapangan B', '09:00:00', '23:00:00', 120000.00),
('Lapangan VIP', '10:00:00', '24:00:00', 250000.00);

-- Insert admin default
INSERT INTO users (username, password, email, user_level)
VALUES ('admin', 'admin123', 'admin@futsal.com', 'admin');

INSERT INTO users (username, password, email, user_level) 
VALUES ('user1', 'user123', 'user1@futsal.com', 'user');


CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    field_id INT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (field_id) REFERENCES futsal_fields(id) ON DELETE CASCADE
);