package domain;

public class Enrollment {
    private int enrollId;
    private int studentId;
    private int sectionId;
    private String status;

    public int getEnrollId() { return enrollId; }
    public void setEnrollId(int enrollId) { this.enrollId = enrollId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[Enroll #%d] Student %d in Section %d (%s)",
                enrollId, studentId, sectionId, status);
    }
}

