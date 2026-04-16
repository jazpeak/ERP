package data;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import testutil.Fakes;

class EnrollmentDaoTest {
    @Test
    void testEnrollAndDropFlow() {
        Fakes.FakeEnrollmentDao enrollmentDao = new Fakes.FakeEnrollmentDao();
        int sectionId = 1;
        int studentId = 1;
        assertTrue(enrollmentDao.enrollStudent(studentId, sectionId));
        assertTrue(enrollmentDao.isEnrolled(studentId, sectionId));
        assertEquals(1, enrollmentDao.countEnrolled(sectionId));
        assertTrue(enrollmentDao.dropStudent(studentId, sectionId));
        assertFalse(enrollmentDao.isEnrolled(studentId, sectionId));
    }
}
