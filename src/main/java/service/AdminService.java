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
}
