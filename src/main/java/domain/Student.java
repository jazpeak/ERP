package domain;

public class Student extends User {
    private String rollNo;
    private String prog;
    private int year;

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getProg() { return prog; }
    public void setProg(String prog) { this.prog = prog; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return String.format("%s (%s, Year %d)", rollNo, prog, year);
    }
}
