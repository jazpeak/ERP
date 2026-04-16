package access;

public enum Role {
    Admin,
    Instructor,
    Student;

    public static Role from(String s) {
        if (s == null) return null;
        return switch (s.trim().toLowerCase()) {
            case "admin" -> Admin;
            case "instructor" -> Instructor;
            case "student" -> Student;
            default -> null;
        };
    }
}