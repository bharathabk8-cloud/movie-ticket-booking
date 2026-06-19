CREATE DATABASE IF NOT EXISTS movie_booking;
USE movie_booking;

CREATE TABLE IF NOT EXISTS movies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    language VARCHAR(50) NOT NULL,
    duration_minutes INT NOT NULL,
    director VARCHAR(255) NOT NULL,
    cast VARCHAR(500) NOT NULL,
    rating VARCHAR(10) NOT NULL,
    imdb_rating DOUBLE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_genre (genre),
    INDEX idx_is_active (is_active)
    );

CREATE TABLE IF NOT EXISTS theatres (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    area VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_city (city)
    );

CREATE TABLE IF NOT EXISTS offers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    offer_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    offer_type VARCHAR(50) NOT NULL,
    discount_value DOUBLE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_offer_code (offer_code)
    );

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    transaction_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_booking_id (booking_id),
    INDEX idx_status (status)
    );

CREATE TABLE IF NOT EXISTS booking_analytics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    analytics_date DATE NOT NULL,
    city VARCHAR(100) NOT NULL,
    total_bookings INT DEFAULT 0,
    total_revenue DOUBLE DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_date (analytics_date)
    );

CREATE USER 'booking_user'@'%' IDENTIFIED BY 'booking_password';
GRANT ALL PRIVILEGES ON movie_booking.* TO 'booking_user'@'%';
FLUSH PRIVILEGES;