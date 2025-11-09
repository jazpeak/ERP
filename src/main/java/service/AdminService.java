package service;

import data.*;
import auth.AuthDao;


public class AdminService {
    private final AuthDao authDao;
    private final StudentDao studentDao;
    private final InstructorDao instructorDao;
    private final CourseDao courseDao;
    private final SectionDao sectionDao;
    private final SettingDao settingsDao;

    public AdminService(AuthDao authDao, StudentDao studentDao, InstructorDao instructorDao,
                        CourseDao courseDao, SectionDao sectionDao, SettingDao settingsDao) {
        this.authDao = authDao;
        this.studentDao = studentDao;
        this.instructorDao = instructorDao;
        this.courseDao = courseDao;
        this.sectionDao = sectionDao;
        this.settingsDao = settingsDao;
    }

    public String toggleMaintenance(boolean on) {
        settingsDao.setMaintenance(on);
        return on ? " Maintenance mode ENABLED" : " Maintenance mode DISABLED";
    }

    public boolean isMaintenanceOn() {
        return settingsDao.isMaintenanceOn();
    }

    public String addStudentUser(String username, String password, String rollNo, String program, int year) {
        int userId = authDao.addUser(username, "Student", password);
        if (userId == -1) return "Failed to add user (duplicate username?)";
        studentDao.addStudent(userId, rollNo, program, year);
        return "Student added: " + username;
    }

    public String addInstructorUser(String username, String password, String department) {
        int userId = authDao.addUser(username, "Instructor", password);
        if (userId == -1) return "Failed to add user (duplicate username?)";
        instructorDao.addInstructor(userId, department);
        return "Instructor added: " + username;
    }

    public String addAdminUser(String username, String password) {
        int userId = authDao.addUser(username, "Admin", password);
        return userId > 0 ? "Admin added: " + username : "Failed to add admin";
    }

    public String addCourse(String code, String title, int credits) {
        if (courseDao.exists(code)) return "Course already exists: " + code;
        boolean ok = courseDao.createCourse(code, title, credits);
        return ok ? "Course created: " + code : "Failed to create course";
    }

    public String assignInstructorToSection(int sectionId, int instructorId) {
        if (instructorDao.getByUserId(instructorId)== null) return "Instructor does not exist with instructor id: "+ instructorId;
        boolean ok = sectionDao.assignInstructor(sectionId, instructorId);
        return ok ? "Instructor assigned." : "Failed to assign instructor.";
    }
}
