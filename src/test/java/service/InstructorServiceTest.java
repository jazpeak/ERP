package service;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import testutil.Fakes;

class InstructorServiceTest {
    private InstructorService svc;
    private Fakes.FakeSettingDao settings;

    @BeforeEach
    void setUp() {
        settings = new Fakes.FakeSettingDao();
        svc = new InstructorService(new Fakes.FakeSectionDao(), new Fakes.FakeEnrollmentDao(), new Fakes.FakeGradeDao(), settings);
    }

    @Test
    void testEnterScoreBlockedByMaintenance() {
        settings.setMaintenance(true);
        String msg = svc.enterScore(1, "Quiz", 10.0);
        assertEquals("Maintenance mode ON: cannot enter grades", msg);
        settings.setMaintenance(false);
    }
}
