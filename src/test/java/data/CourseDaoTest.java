package data;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

import domain.Course;

class CourseDaoTest {

    private DataSource ds;
    private CourseDao dao;
    private static final String TEST_CODE = "CSE999";

    @BeforeEach
    void setUp() throws Exception {
        ds = ErpDataSource.build();
        dao = new CourseDao(ds);
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM courses WHERE code=?")) {
            ps.setString(1, TEST_CODE);
            ps.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM courses WHERE code=?")) {
            ps.setString(1, TEST_CODE);
            ps.executeUpdate();
        }
    }

    @Test
    void testDuplicateCourse() {
        assertTrue(dao.createCourse(TEST_CODE, "JUnit Test Course", 4));
        assertFalse(dao.createCourse(TEST_CODE, "JUnit Test Course", 4));
    }

    @Test
    void testGetCourseByCode() {
        dao.createCourse(TEST_CODE, "Algorithms", 3);
        Course c = dao.getCourseByCode(TEST_CODE);
        assertNotNull(c);
        assertEquals(TEST_CODE, c.getCode());
        assertEquals("Algorithms", c.getTitle());
        assertEquals(3, c.getCredits());
    }

    @Test
    void testExists() {
        assertFalse(dao.exists(TEST_CODE));
        dao.createCourse(TEST_CODE, "Temp", 2);
        assertTrue(dao.exists(TEST_CODE));
    }
}
