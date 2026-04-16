package service;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import testutil.Fakes;

class AdminServiceTest {
    private AdminService svc;
    private Fakes.FakeCourseDao courseDao;
    private Fakes.FakeInstructorDao instructorDao;
    private Fakes.FakeSectionDao sectionDao;
    private Fakes.FakeSettingDao settingDao;
    private Fakes.FakeEnrollmentDao enrollmentDao;

    @BeforeEach
    void setUp() {
        courseDao = new Fakes.FakeCourseDao();
        instructorDao = new Fakes.FakeInstructorDao();
        sectionDao = new Fakes.FakeSectionDao();
        settingDao = new Fakes.FakeSettingDao();
        enrollmentDao = new Fakes.FakeEnrollmentDao();
        svc = new AdminService(new Fakes.FakeAuthDao(), new Fakes.FakeStudentDao(), instructorDao, courseDao, sectionDao, settingDao, enrollmentDao, null, null);
    }

    @Test
    void testAddCourseValidations() {
        assertEquals("Invalid credits", svc.addCourse("X", "Title", -1));
        assertTrue(svc.addCourse("X", "Title", 3).startsWith("Course created"));
        assertTrue(svc.addCourse("X", "Title", 3).startsWith("Course already exists"));
    }
}
