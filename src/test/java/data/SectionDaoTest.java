package data;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import testutil.Fakes;

class SectionDaoTest {
    @Test
    void testCreateGetDeleteSection() {
        Fakes.FakeSectionDao dao = new Fakes.FakeSectionDao();
        int id = dao.createSection("C", null, "Mon", "10-11", "R101", 10, "Spring", 2025, java.sql.Date.valueOf("2025-12-31"));
        assertTrue(id > 0);
        var s = dao.getById(id);
        assertNotNull(s);
        assertEquals("C", s.getCourseCode());
        assertTrue(dao.deleteSection(id));
    }
}
