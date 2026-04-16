package auth;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import testutil.Fakes;

class AuthDaoTest {
    @Test
    void testAddAndFindUser() {
        Fakes.FakeAuthDao dao = new Fakes.FakeAuthDao();
        String username = "junit_user_" + System.nanoTime();
        int id = dao.addUser(username, "Student", "pass123");
        assertTrue(id > 0);
        var u = dao.findByUsername(username);
        assertNotNull(u);
        assertEquals(username, u.getUsername());
    }
}
