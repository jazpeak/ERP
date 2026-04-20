package access;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class RoleTest {
    @Test
    void testRoleFromMappings() {
        assertEquals(Role.Admin, Role.from("Admin"));
        assertEquals(Role.Instructor, Role.from("instructor"));
        assertEquals(Role.Student, Role.from("Student"));
        assertNull(Role.from("unknown"));
        assertNull(Role.from(null));
    }
}
