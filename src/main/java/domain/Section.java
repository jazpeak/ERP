package domain;

public class Section {
    private int sectionId;
    private String courseCode;
    private int inst_Id;
    private String day;
    private String timeSlot;
    private String room;
    private int cap;
    private String sem;
    private int year;

    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public int getInstructorId() { return inst_Id; }
    public void setInstructorId(int instructorId) { this.inst_Id = instructorId; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public int getCapacity() { return cap; }
    public void setCapacity(int capacity) { this.cap = capacity; }

    public String getSemester() { return sem; }
    public void setSemester(String semester) { this.sem = semester; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return String.format("[%s-%d] %s %s @ %s (%d seats)",
                courseCode, sectionId, day, timeSlot, room, cap);
    }
}
