package service;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import testutil.Fakes;

class AuthServiceTest {
    private Fakes.FakeAuthDao authDao;
    private Fakes.FakeSettingDao settingDao;
    private AuthService svc;
    private String username;

    @BeforeEach
    void setUp() {
        authDao = new Fakes.FakeAuthDao();
        settingDao = new Fakes.FakeSettingDao();
        svc = new AuthService(authDao, new Fakes.FakeStudentDao(), new Fakes.FakeInstructorDao(), settingDao);
        username = "junit_login_" + System.nanoTime();
        int id = authDao.addUser(username, "Student", "pass123");
        assertTrue(id > 0);
        settingDao.setMaintenance(false);
    }

    @Test
    void testLockAfterFailures() {
        for (int i = 0; i < 5; i++) {
            assertNull(svc.login(username, "wrong"));
        }
        assertEquals("Account locked", svc.getLastError());
        assertNull(svc.login(username, "pass123"));
    }
}
