package data;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import testutil.Fakes;

class GradeDaoTest {
    @Test
    void testUpdateAndGetGrades() {
        Fakes.FakeGradeDao gradeDao = new Fakes.FakeGradeDao();
        int enrollId = 1;
        gradeDao.UpdateGrade(enrollId, "Quiz", 8.5);
        var g = gradeDao.getGradeForComponent(enrollId, "Quiz");
        assertNotNull(g);
        assertEquals(8.5, g.getScore());
        gradeDao.updateFinalGrade(enrollId, "A");
        var list = gradeDao.getGradesByEnrollment(enrollId);
        assertTrue(list.stream().anyMatch(x -> "A".equals(x.getFinalGrade())));
    }
}
