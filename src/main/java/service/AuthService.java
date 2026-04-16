package service;

import access.Role;
import auth.AuthDao;
import data.SettingDao;
import data.StudentDao;
import data.InstructorDao;
import domain.User;
import domain.Student;
import domain.Instructor;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    public static final class Session {
        public final int userId;
        public final Role role;
        public final String username;

        public Session(int userId, Role role, String username) {
            this.userId = userId;
            this.role = role;
            this.username = username;
        }
    }

    private final AuthDao authDao;
    private final StudentDao studentDao;
    private final InstructorDao instructorDao;
    private final SettingDao settingDao;
    private final java.util.Map<String, Integer> failCounts = new java.util.concurrent.ConcurrentHashMap<>();
    private String lastError;

    private Session current;

    public AuthService(AuthDao authDao, StudentDao studentDao, InstructorDao instructorDao, SettingDao settingDao) {
        this.authDao = authDao;
        this.studentDao = studentDao;
        this.instructorDao = instructorDao;
        this.settingDao = settingDao;
    }

    public Session login(String username, String rawPassword) {
        User u = authDao.findByUsername(username);
        if (u == null) {
            lastError = "Incorrect username or password";
            return null;
        }
        if (u.getStatus() != null && u.getStatus().equalsIgnoreCase("locked")) {
            lastError = "Account locked";
            return null;
        }
        if (!BCrypt.checkpw(rawPassword, u.getPasswordHash())) {
            int c = failCounts.getOrDefault(username, 0) + 1;
            failCounts.put(username, c);
            if (c >= 5) {
                authDao.setStatusForUsername(username, "locked");
                lastError = "Account locked";
            } else {
                lastError = "Incorrect username or password. Attempts left: " + (5 - c);
            }
            return null;
        }
        failCounts.remove(username);
        lastError = null;
        Role r = Role.from(u.getRole());
        current = new Session(u.getUserId(), r, u.getUsername());
        return current;
    }

    public Session getCurrent() {
        return current;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (settingDao != null && settingDao.isMaintenanceOn()) {
            User uRole = authDao.findByUsername(username);
            if (uRole == null || !"Admin".equalsIgnoreCase(uRole.getRole()))
                return false;
        }
        User u = authDao.findByUsername(username);
        if (u == null)
            return false;
        if (!BCrypt.checkpw(oldPassword, u.getPasswordHash()))
            return false;
        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        return authDao.updatePasswordHash(u.getUserId(), hashed);
    }

    public String getLastError() {
        return lastError;
    }

    public boolean unlockUser(String username) {
        failCounts.remove(username);
        return authDao.setStatusForUsername(username, null);
    }

    public int attemptsLeft(String username) {
        Integer c = failCounts.get(username);
        if (c == null)
            return 3;
        int left = 3 - c;
        return left < 0 ? 0 : left;
    }

    public Student loadStudentProfile() {
        if (current == null || current.role != Role.Student)
            return null;
        return studentDao.getByUserId(current.userId);
    }

    public Instructor loadInstructorProfile() {
        if (current == null || current.role != Role.Instructor)
            return null;
        return instructorDao.getByUserId(current.userId);
    }
}
