package domain;

public class Instructor extends User {
    private String dept;
    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }

    @Override
    public String toString() {
        return String.format("Instructor (%s) ", dept);
    }
}
