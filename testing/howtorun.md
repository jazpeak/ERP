# How to Run

## Java Version
- **Java 21** (recommended)

## Database Setup
Create the required MySQL databases:

```sql
CREATE DATABASE auth_db;
CREATE DATABASE erp_db;
```

## Database Connection Settings
Set the following values in `src/main/resources/application.properties` (replace `root` and `your_password` as needed):

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

## Seeding the Databases
You can load the seed SQL files into each database from the command line.

```sh
mysql -u root -p auth_db < auth_db.sql
mysql -u root -p erp_db < erp_db.sql
```

Make sure the seed files are located in the current working directory or provide an absolute path to each file.

The seed scripts will:

- Create all required tables.
- Insert sample data (including a default admin account for login).

After running the seed scripts, you can launch the ERP system and log in as an administrator using the default credentials provided below.

## Compiling and Running via Command Line (Maven)

Prerequisites:
- Maven installed and added to PATH.

### 1. Compile the Project
Run the following command in the project root directory:
```sh
mvn clean install
```

### 2. Run the Application
To run the application directly using Maven:
```sh
mvn exec:java -Dexec.mainClass="ui.Main"

## Launching the UI
To start the ERP system:

1. Open the project in your IDE (IntelliJ / VS Code).
2. Navigate to `src/main/java/ui/Main.java`.
3. Run the `main()` method in `Main.java`.
4. The Login UI window will appear.

## Default Accounts (After Seeding)

- **Admin**

	- Username: `admin1` / Password: `admin123`

- **Students** (password = username)

	- `stu1` / `stu1`
	- `stu2` / `stu2`
	- `amit` / `amit`
	- `bhavya` / `bhavya`
	- `celine` / `celine`
	- `devyaansh` / `devyaansh`
	- `esha` / `esha`


- **Instructors** (password = username)

	- `inst1` / `inst1`
	- `sambuddho` / `sambuddho`
	- `subhajit` / `subhajit`
	- `pravesh` / `pravesh`
	- `paro` / `paro`
	- `sonal` / `sonal`




