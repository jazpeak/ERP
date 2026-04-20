package service;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import testutil.Fakes;

class StudentServiceTest {
    private StudentService svc;
    private Fakes.FakeSectionDao sectionDao;
    private Fakes.FakeEnrollmentDao enrollmentDao;
    private Fakes.FakeCourseDao courseDao;
    private Fakes.FakeSettingDao settingDao;

    @BeforeEach
    void setUp() {
        courseDao = new Fakes.FakeCourseDao();
        sectionDao = new Fakes.FakeSectionDao();
        enrollmentDao = new Fakes.FakeEnrollmentDao();
        settingDao = new Fakes.FakeSettingDao();
        svc = new StudentService(courseDao, sectionDao, enrollmentDao, new Fakes.FakeGradeDao(), settingDao, new Fakes.FakeInstructorDao());
        courseDao.createCourse("C1", "Title", 3);
        sectionDao.createSection("C1", null, "Thu", "12-13", "R404", 1, "Spring", 2025, java.sql.Date.valueOf("2025-12-31"));
    }

    @Test
    void testRegisterAlreadyAndFull() {
        int sid = sectionDao.getAllSections().get(0).getSectionId();
        assertEquals("Registered", svc.registerSection(1, sid));
        assertEquals("Already registered", svc.registerSection(1, sid));
        int sid2 = sectionDao.createSection("C1", null, "Thu", "12-13", "R404", 1, "Spring", 2025, java.sql.Date.valueOf("2025-12-31"));
        assertEquals("Registered", svc.registerSection(1, sid2));
        var msg = svc.registerSection(2, sid2);
        assertTrue(msg.equals("Section full") || msg.equals("No seats left"));
    }

    @Test
    void testDropDeadlineValidation() {
        int sid = sectionDao.getAllSections().get(0).getSectionId();
        assertEquals("Registered", svc.registerSection(1, sid));
        settingDao.updateSetting("drop_deadline", java.time.LocalDate.now().minusDays(1).toString());
        assertEquals("Drop deadline passed", svc.dropSection(1, sid));
        settingDao.updateSetting("drop_deadline", java.time.LocalDate.now().plusDays(1).toString());
        assertEquals("Dropped", svc.dropSection(1, sid));
    }
}
