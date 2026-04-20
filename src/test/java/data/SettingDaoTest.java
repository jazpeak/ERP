package data;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import testutil.Fakes;

class SettingDaoTest {
    private Fakes.FakeSettingDao dao;

    @BeforeEach
    void setUp() {
        dao = new Fakes.FakeSettingDao();
    }

    @Test
    void testMaintenanceToggle() {
        dao.setMaintenance(true);
        assertTrue(dao.isMaintenanceOn());
        dao.setMaintenance(false);
        assertFalse(dao.isMaintenanceOn());
    }

    @Test
    void testUpdateAndGetSetting() {
        dao.updateSetting("notice", "Hello");
        var s = dao.getSetting("notice");
        assertNotNull(s);
        assertEquals("Hello", s.getValue());
    }
}
