-- Auth Database Schema
-- Run this script to create the required tables in auth_db

-- Create users_auth table
CREATE TABLE IF NOT EXISTS users_auth (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert a default admin user (password: admin123)
-- Password hash is BCrypt hash of "admin123"
INSERT IGNORE INTO users_auth (username, role, password_hash) VALUES
('admin', 'Admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye1IVI8.HsSfVsF7jKDU8SJ3.PJ2dQdFu');
