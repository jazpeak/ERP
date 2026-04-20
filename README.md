# 🎓 University ERP System

A robust, Java-based Enterprise Resource Planning (ERP) desktop application designed for university administration, instructors, and students. Built with Java Swing, the application features a modern UI using FlatLaf and connects to a dual-database MySQL backend.

## � Demo Video
Check out the system in action:

<video width="800" controls>
  <source src="https://raw.githubusercontent.com/jazpeak/ERP/master/ERPDemoVideo.mp4" type="video/mp4">
</video>

*(If the video player doesn't load instantly, [click here to view the demo video](https://github.com/jazpeak/ERP/blob/master/ERPDemoVideo.mp4))*

## �🚀 Features

- **Role-Based Access Controls**: Distinct dashboards, modules, and workflows for Admins, Instructors, and Students.
- **Modern UI**: Clean, responsive layout employing [FlatLaf](https://www.formdev.com/flatlaf/) for dynamic themes, and MigLayout for precision spacing.
- **Secure Authentication**: Dual database model isolating user credentials (`auth_db`) from application business logic (`erp_db`). Passwords comprehensively hashed utilizing jBCrypt.
- **High Performance**: Employs HikariCP for streamlined SQL connection pooling.

## 🛠️ Technology Stack

- **Java 21**
- **Maven**
- **MySQL / MariaDB**
- **Java Swing & MigLayout**
- **HikariCP** (Connection Pooling)
- **jBCrypt** (Password Security)
- **JUnit 5** (Testing)

## ⚙️ Setup & Installation

### 1. Database Setup
Create the required MySQL databases:
```sql
CREATE DATABASE auth_db;
CREATE DATABASE erp_db;
```

### 2. Configure Credentials
Update the connection bindings in `src/main/resources/application.properties` to match your local MySQL installation:
```properties
# Authentication Database
auth.jdbc.url=jdbc:mysql://localhost:3306/auth_db
auth.jdbc.user=root
auth.jdbc.pass=your_password

# ERP Database
erp.jdbc.url=jdbc:mysql://localhost:3306/erp_db
erp.jdbc.user=root
erp.jdbc.pass=your_password
```

### 3. Database Seeding
Initialize the schema structures and inject initial sample data from your terminal:
```sh
mysql -u root -p auth_db < src/main/resources/init_auth_db.sql
mysql -u root -p erp_db < src/main/resources/init_erp_db.sql
```
*(This maps the default credentials required below).*

## 🏃 Running the Application

### Via Command Line (Maven)
Make sure Maven is on your PATH, then jump to the root directory mapping this repo:

```sh
# 1. Compilation
mvn clean install

# 2. Execution
mvn exec:java -Dexec.mainClass="ui.Main"
```

### Via IDE
1. Open the project configuration in your preferred environment (IntelliJ IDEA / VS Code / Eclipse).
2. Configure **Java 21** as your SDK framework.
3. Locate and execute the `main()` frame method in `src/main/java/ui/Main.java`.

## 🔑 Default Seeded Accounts

System tests come heavily seeded. Log in contextually using the credentials below (where unspecified, passwords match the usernames):

- **Admin Account**: `admin1` / `admin123`

- **Instructor Accounts**: 
  `inst1`, `sambuddho`, `subhajit`, `pravesh`, `paro`, `sonal` 
  *(Example login: user `inst1`, password `inst1`)*

- **Student Accounts**: 
  `stu1`, `stu2`, `amit`, `bhavya`, `celine`, `devyaansh`, `esha`

## 🧪 Testing

The codebase is highly coupled to programmatic tests extending DAO and Service validation parameters:
```sh
mvn test
```
