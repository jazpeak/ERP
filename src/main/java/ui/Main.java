package ui;

import com.formdev.flatlaf.FlatLightLaf;
import auth.AuthDataSource;
import auth.AuthDao;
import data.*;
import service.*;

import javax.sql.DataSource;

public class Main {
    public static AuthService AUTH;
    public static StudentService STUDENT;
    public static InstructorService INSTRUCTOR;
    public static AdminService ADMIN;

    public static void showLogin() {
        new ui.auth.LoginFrame(AUTH, STUDENT, INSTRUCTOR, ADMIN).setVisible(true);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();

        DataSource authDS = AuthDataSource.build();
        DataSource erpDS = ErpDataSource.build();

        AuthDao authDao = new AuthDao(authDS);
        CourseDao courseDao = new CourseDao(erpDS);
        SectionDao sectionDao = new SectionDao(erpDS);
        StudentDao studentDao = new StudentDao(erpDS);
        InstructorDao instructorDao = new InstructorDao(erpDS);
        EnrollmentDao enrollmentDao = new EnrollmentDao(erpDS);
        GradeDao gradeDao = new GradeDao(erpDS);
        SettingDao settingDao = new SettingDao(erpDS);

        AUTH = new AuthService(authDao, studentDao, instructorDao, settingDao);
        STUDENT = new StudentService(courseDao, sectionDao, enrollmentDao, gradeDao, settingDao, instructorDao);
        INSTRUCTOR = new InstructorService(sectionDao, enrollmentDao, gradeDao, settingDao);
        ADMIN = new AdminService(authDao, studentDao, instructorDao, courseDao, sectionDao, settingDao, enrollmentDao,
                authDS, erpDS);

        showLogin();
    }
}
