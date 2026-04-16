# University ERP

A comprehensive, robust University ERP system built with Java 21, Swing (FlatLaf & MigLayout), and MySQL. The system seamlessly handles authentication, student enrollment, course management, section administration, and grade tracking with support for multiple user roles (Admin, Instructor, and Student).

## 🌟 Features

* **Authentication & Access Control**: Secure login system with BCrypt password hashing, role-based access, and automatic account lockout after consecutive failed attempts.
* **Admin Module**: Manage system-wide settings like maintenance mode and drop deadlines, create and oversee courses, and manage user accounts.
* **Instructor Module**: Oversee assigned sections, manage grading weights by component (e.g., Quizzes, Midterms, Finals), and publish final letter grades.
* **Student Module**: Explore available courses, register for sections with real-time capacity checks, drop courses before deadlines, and view grades.
* **Robust Testing Suite**: Comprehensive test coverage using JUnit 5, featuring in-memory mock DAOs for lightning-fast business logic validation, and database integration tests for data persistence accuracy.

## 🛠️ Technology Stack

* **Language**: Java 21
* **Build Tool**: Maven
* **UI Framework**: Swing with **FlatLaf** (for modern, rich theming) and **MigLayout** (for responsive layout management)
* **Database**: MySQL 8+
* **Data Access**: JDBC with **HikariCP** Connection Pooling
* **Security**: jBcrypt (Password Hashing)
* **Testing**: JUnit 5 (Jupiter)
  
## 🚀 Demo
You can see a live demonstration of the application here: [View Demo Video](https://drive.google.com/file/d/1RVZ__JDxJPFS3n_UrCENjgsyhNFcJykD/view?usp=sharing)
