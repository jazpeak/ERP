package org.example;

import auth.AuthDataSource;
import auth.AuthDao;
import data.CourseDao;
import data.SectionDao;
import data.ErpDataSource;
import data.StudentDao;
import data.EnrollmentDao;
import domain.Enrollment;
import domain.User;
import domain.Course;
import domain.Section;
import domain.Student;

import javax.sql.DataSource;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataSource authDS = AuthDataSource.build();
        DataSource erpDS = ErpDataSource.build();

        AuthDao authDao = new AuthDao(authDS);
        User user = authDao.findByUsername("admin1");
        if (user != null)
            System.out.println("Found user: " + user.getUsername() + " (" + user.getRole() + ")");
        else
            System.out.println("User not found");

        CourseDao courseDao = new CourseDao(erpDS);
        List<Course> courses = courseDao.getAllCourses();
        System.out.println("\n Courses:");
        for (Course c : courses) System.out.println(" - " + c);

        SectionDao sectionDao = new SectionDao(erpDS);
        List<Section> sections = sectionDao.getAllSections();
        System.out.println("\n Sections:");
        for (Section s : sections) System.out.println(" - " + s);

        StudentDao studentDao = new StudentDao(erpDS);
        System.out.println("\nStudents:");
        for (Student s : studentDao.getAllStudents())
            System.out.println(" - " + s);

        EnrollmentDao enrollmentDao = new EnrollmentDao(erpDS);
        System.out.println("\n Enrollments:");
        for (Enrollment e : enrollmentDao.getAllEnrollments())
            System.out.println(" - " + e);

        System.out.println("\nRegistering Student 2 in Section 2...");
        enrollmentDao.enrollStudent(2, 2);

    }
}
