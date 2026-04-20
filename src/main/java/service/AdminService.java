package service;

import data.*;
import auth.AuthDao;
import domain.User;
import java.io.File;
import javax.sql.DataSource;

public class AdminService {
    private final AuthDao authDao;
    private final StudentDao studentDao;
    private final InstructorDao instructorDao;
    private final CourseDao courseDao;
    private final SectionDao sectionDao;
    private final SettingDao settingsDao;
    private final EnrollmentDao enrollmentDao;
    private final DataSource authDs;
    private final DataSource erpDs;

    public AdminService(AuthDao authDao, StudentDao studentDao, InstructorDao instructorDao,
            CourseDao courseDao, SectionDao sectionDao, SettingDao settingsDao,
            EnrollmentDao enrollmentDao, DataSource authDs, DataSource erpDs) {
        this.authDao = authDao;
        this.studentDao = studentDao;
        this.instructorDao = instructorDao;
        this.courseDao = courseDao;
        this.sectionDao = sectionDao;
        this.settingsDao = settingsDao;
        this.enrollmentDao = enrollmentDao;
        this.authDs = authDs;
        this.erpDs = erpDs;
    }

    public String toggleMaintenance(boolean on) {
        settingsDao.setMaintenance(on);
        return on ? " Maintenance mode ENABLED" : " Maintenance mode DISABLED";
    }

    public boolean isMaintenanceOn() {
        return settingsDao.isMaintenanceOn();
    }

    public String getNotice() {
        var s = settingsDao.getSetting("notice");
        return s != null ? s.getValue() : "";
    }

    public void setNotice(String text) {
        settingsDao.updateSetting("notice", text);
    }

    public String addStudentUser(String username, String password, String rollNo, String program, int year) {
        if (username == null || username.isBlank() || password == null || password.isBlank())
            return "Username and password required";
        if (rollNo == null || rollNo.isBlank())
            return "Roll number required";
        if (studentDao.existsByRoll(rollNo))
            return "Roll already exists";
        User existing = authDao.findByUsername(username);
        if (existing != null)
            return "Username already exists";
        int userId = authDao.addUser(username, "Student", password);
        if (userId <= 0)
            return "Failed to add student user";
        boolean success = studentDao.addStudent(userId, rollNo, program, year);
        if (!success) {
            return "Failed to add student details to ERP database (check logs)";
        }
        return "Student added";
    }

    public String addInstructorUser(String username, String password, String department) {
        if (username == null || username.isBlank() || password == null || password.isBlank())
            return "Username and password required";
        User existing = authDao.findByUsername(username);
        if (existing != null)
            return "Username already exists";
        int userId = authDao.addUser(username, "Instructor", password);
        if (userId <= 0)
            return "Failed to add instructor user";
        instructorDao.addInstructor(userId, department);
        return "Instructor added";
    }

    public String addAdminUser(String username, String password) {
        User existing = authDao.findByUsername(username);
        if (existing != null)
            return "Username already exists";
        int userId = authDao.addUser(username, "Admin", password);
        return userId > 0 ? "Admin added: " + username : "Failed to add admin";
    }

    public String setPasswordForUser(String username, String newPassword) {
        User existing = authDao.findByUsername(username);
        if (existing == null)
            return "User not found";
        boolean ok = authDao.setPasswordForUsername(username, newPassword);
        return ok ? "Password set for " + username : "Failed to set password";
    }

    public String unlockUser(String username) {
        User existing = authDao.findByUsername(username);
        if (existing == null)
            return "User not found";
        boolean ok = authDao.setStatusForUsername(username, "active");
        return ok ? "Account unlocked for " + username : "Failed to unlock account";
    }

    public String lockUser(String username) {
        User existing = authDao.findByUsername(username);
        if (existing == null)
            return "User not found";
        boolean ok = authDao.setStatusForUsername(username, "locked");
        return ok ? "Account locked for " + username : "Failed to lock account";
    }

    public String addCourse(String code, String title, int credits) {
        if (credits < 0)
            return "Invalid credits";
        if (courseDao.exists(code))
            return "Course already exists: " + code;
        boolean ok = courseDao.createCourse(code, title, credits);
        return ok ? "Course created: " + code : "Failed to create course";
    }

    public int createSection(String courseCode, Integer instructorId, String day, String timeSlot, String room,
            int capacity, String semester, int year, java.sql.Date regDeadline) {
        if (capacity < 0)
            return -1;

        // Validate course code
        if (!courseDao.exists(courseCode)) {
            return -3; // Error code for invalid course
        }

        // Validate instructor if provided
        if (instructorId != null && instructorId > 0) {
            if (instructorDao.getByUserId(instructorId) == null) {
                return -2; // Error code for invalid instructor
            }
        }

        return sectionDao.createSection(courseCode, instructorId, day, timeSlot, room, capacity, semester, year,
                regDeadline);
    }

    public String assignInstructorToSection(int sectionId, int instructorId) {
        if (instructorDao.getByUserId(instructorId) == null)
            return "Instructor does not exist with instructor id: " + instructorId;
        boolean ok = sectionDao.assignInstructor(sectionId, instructorId);
        return ok ? "Instructor assigned." : "Failed to assign instructor.";
    }

    public String removeSection(int sectionId) {
        if (enrollmentDao.countEnrolled(sectionId) > 0)
            return "Cannot remove: students are enrolled";
        var s = sectionDao.getById(sectionId);
        if (s == null)
            return "Section not found";
        if (s.getInstructorId() > 0)
            sectionDao.clearInstructor(sectionId);
        boolean ok = sectionDao.deleteSection(sectionId);
        return ok ? "Section removed: " + sectionId : "Failed to remove section";
    }

    public java.util.List<domain.Section> listSections() {
        return sectionDao.getAllSections();
    }

    public java.util.List<domain.Course> listCourses() {
        return courseDao.getAllCourses();
    }

    public java.util.List<User> listUsersByRole(String role) {
        java.util.List<User> all = authDao.getAllUsers();
        java.util.List<User> out = new java.util.ArrayList<>();
        for (User u : all) {
            if (u.getRole() != null && u.getRole().equalsIgnoreCase(role)) {
                if ("Student".equalsIgnoreCase(role)) {
                    domain.Student s = studentDao.getByUserId(u.getUserId());
                    if (s != null) {
                        s.setUsername(u.getUsername());
                        s.setRole(u.getRole());
                        s.setStatus(u.getStatus());
                        out.add(s);
                    } else {
                        out.add(u);
                    }
                } else {
                    out.add(u);
                }
            }
        }
        return out;
    }

    public byte[] backupDatabase(String dbName) {
        try {
            // pick datasource based on DB name
            DataSource ds = dbName.equals("auth_db") ? authDs : erpDs;

            StringBuilder sb = new StringBuilder();
            String schema = dbName;
            try (var conn0 = ds.getConnection();
                    var ps0 = conn0.prepareStatement("SELECT DATABASE()");
                    var rs0 = ps0.executeQuery()) {
                if (rs0.next() && rs0.getString(1) != null && !rs0.getString(1).isBlank())
                    schema = rs0.getString(1);
            }
            sb.append("-- Backup for database ").append(schema).append("\n");
            sb.append("SET FOREIGN_KEY_CHECKS=0;\n");

            try (var conn = ds.getConnection()) {
                try (var psTables = conn.prepareStatement(
                        "SELECT table_name FROM information_schema.tables WHERE table_schema = ? ORDER BY table_name")) {
                    psTables.setString(1, schema);
                    try (var rsTables = psTables.executeQuery()) {

                        while (rsTables.next()) {
                            String table = rsTables.getString(1);

                            // CREATE TABLE (qualified)
                            try (var psCreate = conn
                                    .prepareStatement("SHOW CREATE TABLE `" + schema + "`.`" + table + "`")) {
                                try (var rsCreate = psCreate.executeQuery()) {
                                    if (rsCreate.next()) {
                                        sb.append("\nDROP TABLE IF EXISTS `").append(table).append("`;\n");
                                        sb.append(rsCreate.getString(2)).append(";\n\n");
                                    }
                                }
                            }

                            // ROWS (qualified)
                            try (var psData = conn.prepareStatement("SELECT * FROM `" + schema + "`.`" + table + "`")) {
                                try (var data = psData.executeQuery()) {
                                    var meta = data.getMetaData();
                                    int cols = meta.getColumnCount();

                                    while (data.next()) {
                                        sb.append("INSERT INTO `").append(table).append("` VALUES (");
                                        for (int i = 1; i <= cols; i++) {
                                            Object val = data.getObject(i);
                                            if (val == null)
                                                sb.append("NULL");
                                            else
                                                sb.append("'").append(val.toString().replace("'", "''")).append("'");

                                            if (i < cols)
                                                sb.append(",");
                                        }
                                        sb.append(");\n");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            sb.append("SET FOREIGN_KEY_CHECKS=1;\n");

            return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public String restoreDatabase(String dbName, File sqlFile) {
        try {
            DataSource ds = dbName.equals("auth_db") ? authDs : erpDs;

            try (var conn = ds.getConnection()) {
                conn.setAutoCommit(false);

                var content = new String(
                        java.nio.file.Files.readAllBytes(sqlFile.toPath()),
                        java.nio.charset.StandardCharsets.UTF_8);

                var statements = content.split(";");

                try (var stmt = conn.createStatement()) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS=0");
                    stmt.execute("USE `" + dbName + "`");
                    for (String s : statements) {
                        s = s.trim();
                        if (!s.isEmpty() && !s.startsWith("--")) {
                            stmt.execute(s);
                        }
                    }
                    stmt.execute("SET FOREIGN_KEY_CHECKS=1");
                }

                conn.commit();
                return "Restore successful for database: " + dbName;
            }

        } catch (Exception e) {
            return "Restore failed for " + dbName + ": " + e.getMessage();
        }
    }

    public byte[] backupAllDatabases() {
        try {
            var out = new java.io.ByteArrayOutputStream();
            var zip = new java.util.zip.ZipOutputStream(out);

            String authName = "auth_db";
            try (var c = authDs.getConnection();
                    var ps = c.prepareStatement("SELECT DATABASE()");
                    var r = ps.executeQuery()) {
                if (r.next() && r.getString(1) != null && !r.getString(1).isBlank())
                    authName = r.getString(1);
            }
            zip.putNextEntry(new java.util.zip.ZipEntry(authName + ".sql"));
            zip.write(backupDatabase(authName));
            zip.closeEntry();

            String erpName = "erp_db";
            try (var c = erpDs.getConnection();
                    var ps = c.prepareStatement("SELECT DATABASE()");
                    var r = ps.executeQuery()) {
                if (r.next() && r.getString(1) != null && !r.getString(1).isBlank())
                    erpName = r.getString(1);
            }
            zip.putNextEntry(new java.util.zip.ZipEntry(erpName + ".sql"));
            zip.write(backupDatabase(erpName));
            zip.closeEntry();

            zip.close();
            return out.toByteArray();

        } catch (Exception e) {
            return new byte[0];
        }
    }

    public String restoreAllDatabases(File zipFile) {
        try (var zip = new java.util.zip.ZipFile(zipFile)) {

            String authName = "auth_db";
            try (var c = authDs.getConnection();
                    var ps = c.prepareStatement("SELECT DATABASE()");
                    var r = ps.executeQuery()) {
                if (r.next() && r.getString(1) != null && !r.getString(1).isBlank())
                    authName = r.getString(1);
            }
            String erpName = "erp_db";
            try (var c = erpDs.getConnection();
                    var ps = c.prepareStatement("SELECT DATABASE()");
                    var r = ps.executeQuery()) {
                if (r.next() && r.getString(1) != null && !r.getString(1).isBlank())
                    erpName = r.getString(1);
            }

            var auth = zip.getEntry(authName + ".sql");
            if (auth == null)
                auth = zip.getEntry("auth_db.sql");
            var erp = zip.getEntry(erpName + ".sql");
            if (erp == null)
                erp = zip.getEntry("erp_db.sql");

            if (auth != null)
                restoreDatabase("auth_db", streamToTemp(zip.getInputStream(auth)));

            if (erp != null)
                restoreDatabase("erp_db", streamToTemp(zip.getInputStream(erp)));

            return "Full restore completed successfully.";

        } catch (Exception e) {
            return "Full restore failed: " + e.getMessage();
        }
    }

    private java.io.File streamToTemp(java.io.InputStream in) throws java.io.IOException {
        java.io.File tmp = java.io.File.createTempFile("import", ".sql");
        try (var fos = new java.io.FileOutputStream(tmp)) {
            in.transferTo(fos);
        }
        return tmp;
    }

    public int getTotalCourses() {
        return courseDao.getAllCourses().size();
    }

    public int getTotalSections() {
        return sectionDao.getAllSections().size();
    }

    public int getTotalEnrollments() {
        return enrollmentDao.getTotalEnrollments();
    }

    public void setMaintenanceMode(boolean on) {
        settingsDao.setMaintenance(on);
    }

    public java.util.List<domain.Course> getAllCourses() {
        return courseDao.getAllCourses();
    }

    public java.util.List<domain.Section> getAllSections() {
        return sectionDao.getAllSections();
    }
}
