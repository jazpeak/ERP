-- ERP Database Schema
-- Run this script to create the required tables in erp_db

-- Create students table
CREATE TABLE IF NOT EXISTS students (
    user_id INT PRIMARY KEY,
    roll_no VARCHAR(50) UNIQUE NOT NULL,
    prog VARCHAR(100),
    year INT,
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id) ON DELETE CASCADE
);

-- Create instructors table
CREATE TABLE IF NOT EXISTS instructors (
    user_id INT PRIMARY KEY,
    department VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id) ON DELETE CASCADE
);

-- Create courses table
CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    credits INT DEFAULT 3
);

-- Create sections table
CREATE TABLE IF NOT EXISTS sections (
    section_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL,
    instructor_id INT,
    day VARCHAR(20),
    time_slot VARCHAR(50),
    room VARCHAR(50),
    capacity INT DEFAULT 30,
    seats_left INT DEFAULT 0,
    sem VARCHAR(20),
    year INT,
    reg_deadline DATE,
    FOREIGN KEY (course_code) REFERENCES courses(code) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id) ON DELETE SET NULL
);

-- Create enrollments table
CREATE TABLE IF NOT EXISTS enrollments (
    enroll_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'Enrolled',
    enrolled_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, section_id)
);

-- Create grades table
CREATE TABLE IF NOT EXISTS grades (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL,
    component VARCHAR(50) NOT NULL,
    score DOUBLE DEFAULT 0,
    final_grade VARCHAR(5),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enroll_id) ON DELETE CASCADE,
    UNIQUE KEY unique_grade (enrollment_id, component)
);

-- Create section_weights table
CREATE TABLE IF NOT EXISTS section_weights (
    weight_id INT AUTO_INCREMENT PRIMARY KEY,
    section_id INT NOT NULL,
    component VARCHAR(50) NOT NULL,
    weight INT NOT NULL,
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
    UNIQUE KEY unique_weight (section_id, component)
);

-- Create settings table
CREATE TABLE IF NOT EXISTS settings (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value TEXT
);

-- Insert default settings
INSERT IGNORE INTO settings (setting_key, setting_value) VALUES
('maintenance_mode', 'false'),
('notice', 'Welcome to the University ERP System'),
('drop_deadline', '2025-12-31');
