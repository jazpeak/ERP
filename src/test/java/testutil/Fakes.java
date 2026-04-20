package testutil;

import auth.AuthDao;
import data.*;
import domain.*;

import javax.sql.DataSource;
import java.util.*;

public final class Fakes {

    public static class FakeSettingDao extends SettingDao {
        private final Map<String, String> store = new HashMap<>();
        public FakeSettingDao() { super((DataSource) null); }
        @Override public void setMaintenance(boolean on){ store.put("maintenance", on ? "on" : "off"); }
        @Override public boolean isMaintenanceOn(){ return "on".equalsIgnoreCase(store.get("maintenance")); }
        @Override public void updateSetting(String key, String value){ store.put(key, value); }
        @Override public Setting getSetting(String key){ String v = store.get(key); if (v==null) return null; Setting s=new Setting(); s.setKey(key); s.setValue(v); return s; }
    }

    public static class FakeAuthDao extends AuthDao {
        private final Map<String, User> users = new HashMap<>();
        public FakeAuthDao(){ super((DataSource) null); }
        @Override public User findByUsername(String username){ return users.get(username); }
        @Override public java.util.List<User> getAllUsers(){ return new ArrayList<>(users.values()); }
        @Override public int addUser(String username, String role, String rawPassword){
            int id = users.size() + 1;
            User u = new User();
            u.setUserId(id); u.setUsername(username); u.setRole(role);
            u.setPasswordHash(org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt()));
            users.put(username, u); return id;
        }
        @Override public boolean updatePasswordHash(int userId, String newHash){
            for (User u : users.values()) { if (u.getUserId()==userId){ u.setPasswordHash(newHash); return true; } }
            return false;
        }
        @Override public boolean setPasswordForUsername(String username, String rawPassword){
            User u = users.get(username); if (u==null) return false;
            u.setPasswordHash(org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt()));
            return true;
        }
        @Override public boolean setStatusForUsername(String username, String status){
            User u = users.get(username); if (u==null) return false; u.setStatus(status); return true;
        }
    }

    public static class FakeCourseDao extends CourseDao {
        private final Map<String, Course> courses = new HashMap<>();
        public FakeCourseDao(){ super((DataSource) null); }
        @Override public List<Course> getAllCourses(){ return new ArrayList<>(courses.values()); }
        @Override public boolean exists(String code){ return courses.containsKey(code); }
        @Override public boolean createCourse(String code, String title, int credits){
            if (exists(code)) return false; Course c = new Course(); c.setCode(code); c.setTitle(title); c.setCredits(credits); courses.put(code,c); return true;
        }
        @Override public Course getCourseByCode(String code){ return courses.get(code); }
    }

    public static class FakeSectionDao extends SectionDao {
        private final Map<Integer, Section> sections = new HashMap<>();
        private int nextId = 1;
        public FakeSectionDao(){ super((DataSource) null); }
        @Override public List<Section> getAllSections(){ return new ArrayList<>(sections.values()); }
        @Override public List<Section> getSectionsByInstructor(int instructorId){
            List<Section> list = new ArrayList<>();
            for (Section s : sections.values()) if (s.getInstructorId()==instructorId) list.add(s);
            return list;
        }
        @Override public boolean assignInstructor(int sectionId, int instructorId){ Section s = sections.get(sectionId); if (s==null) return false; s.setInstructorId(instructorId); return true; }
        @Override public boolean updateSeats(int sectionId, int delta){ Section s = sections.get(sectionId); if (s==null) return false; s.setSeatsLeft(s.getSeatsLeft()+delta); return true; }
        @Override public int createSection(String courseCode, Integer instructorId, String day, String timeSlot, String room, int capacity, String semester, int year, java.sql.Date regDeadline){
            int id = nextId++; Section s = new Section(); s.setSectionId(id); s.setCourseCode(courseCode);
            s.setInstructorId(instructorId==null?0:instructorId); s.setDay(day); s.setTimeSlot(timeSlot); s.setRoom(room);
            s.setCapacity(capacity); s.setSeatsLeft(capacity); s.setSemester(semester); s.setYear(year); s.setRegDeadline(regDeadline); sections.put(id,s); return id;
        }
        @Override public Section getById(int sectionId){ return sections.get(sectionId); }
        @Override public boolean clearInstructor(int sectionId){ Section s = sections.get(sectionId); if (s==null) return false; s.setInstructorId(0); return true; }
        @Override public boolean deleteSection(int sectionId){ return sections.remove(sectionId)!=null; }
    }

    public static class FakeInstructorDao extends InstructorDao {
        private final Map<Integer, Instructor> data = new HashMap<>();
        public FakeInstructorDao(){ super((DataSource) null); }
        @Override public Instructor getByUserId(int userId){ return data.get(userId); }
        @Override public void addInstructor(int userId, String department){ Instructor i = new Instructor(); i.setUserId(userId); i.setDept(department); data.put(userId,i); }
        @Override public String getNameByUserId(int userId){ Instructor i = data.get(userId); return i==null?null:"inst"+userId; }
    }

    public static class FakeStudentDao extends StudentDao {
        private final Map<Integer, Student> data = new HashMap<>();
        public FakeStudentDao(){ super((DataSource) null); }
        @Override public Student getByUserId(int userId){ return data.get(userId); }
        @Override public boolean addStudent(int userId, String rollNo, String program, int year){ Student s=new Student(); s.setUserId(userId); s.setRollNo(rollNo); s.setProg(program); s.setYear(year); data.put(userId,s); return true; }
        @Override public boolean existsByRoll(String rollNo){ return data.values().stream().anyMatch(s->rollNo.equals(s.getRollNo())); }
    }

    public static class FakeEnrollmentDao extends EnrollmentDao {
        private final Map<Integer, Enrollment> enrollments = new HashMap<>();
        private int nextId = 1;
        public FakeEnrollmentDao(){ super((DataSource) null); }
        @Override public boolean enrollStudent(int studentId, int sectionId){
            for (Enrollment e : enrollments.values()) if (e.getStudentId()==studentId && e.getSectionId()==sectionId){ e.setStatus("enrolled"); return true; }
            Enrollment e = new Enrollment(); e.setEnrollId(nextId++); e.setStudentId(studentId); e.setSectionId(sectionId); e.setStatus("enrolled"); enrollments.put(e.getEnrollId(), e); return true;
        }
        @Override public boolean dropStudent(int studentId, int sectionId){ for (Enrollment e: enrollments.values()) if (e.getStudentId()==studentId && e.getSectionId()==sectionId){ e.setStatus("dropped"); return true; } return false; }
        @Override public Integer getEnrollmentId(int studentId, int sectionId){ for (Enrollment e: enrollments.values()) if (e.getStudentId()==studentId && e.getSectionId()==sectionId) return e.getEnrollId(); return null; }
        @Override public boolean deleteEnrollmentById(int enrollId){ return enrollments.remove(enrollId)!=null; }
        @Override public int countEnrolled(int sectionId){ int c=0; for (Enrollment e: enrollments.values()) if (e.getSectionId()==sectionId && "enrolled".equals(e.getStatus())) c++; return c; }
        @Override public boolean isEnrolled(int studentId, int sectionId){ for (Enrollment e: enrollments.values()) if (e.getStudentId()==studentId && e.getSectionId()==sectionId && "enrolled".equals(e.getStatus())) return true; return false; }
        @Override public java.util.List<Enrollment> getBySection(int sectionId){ List<Enrollment> list=new ArrayList<>(); for (Enrollment e: enrollments.values()) if (e.getSectionId()==sectionId && "enrolled".equals(e.getStatus())) list.add(e); return list; }
        @Override public java.util.List<Enrollment> getByStudent(int studentId){ List<Enrollment> list=new ArrayList<>(); for (Enrollment e: enrollments.values()) if (e.getStudentId()==studentId && "enrolled".equals(e.getStatus())) list.add(e); return list; }
    }

    public static class FakeGradeDao extends GradeDao {
        private final Map<Integer, List<Grade>> store = new HashMap<>();
        private final Map<Integer, Map<String,Integer>> weights = new HashMap<>();
        public FakeGradeDao(){ super((DataSource) null); }
        @Override public java.util.List<Grade> getGradesByEnrollment(int enrollId){ return new ArrayList<>(store.getOrDefault(enrollId, Collections.emptyList())); }
        @Override public void UpdateGrade(int enrollmentId, String component, double score){
            List<Grade> list = store.computeIfAbsent(enrollmentId, k->new ArrayList<>());
            for (Grade g : list) if (Objects.equals(g.getComponent(), component)){ g.setScore(score); return; }
            Grade g = new Grade(); g.setEnrollmentId(enrollmentId); g.setComponent(component); g.setScore(score); list.add(g);
        }
        @Override public Grade getGradeForComponent(int enrollmentId, String component){
            for (Grade g : store.getOrDefault(enrollmentId, Collections.emptyList())) if (Objects.equals(g.getComponent(), component)) return g; return null;
        }
        @Override public void updateFinalGrade(int enrollmentId, String letter){
            List<Grade> list = store.computeIfAbsent(enrollmentId, k->new ArrayList<>());
            if (list.isEmpty()){ Grade g = new Grade(); g.setEnrollmentId(enrollmentId); g.setComponent("Final"); g.setFinalGrade(letter); list.add(g); }
            else { list.get(0).setFinalGrade(letter); }
        }
        @Override public void deleteGradesForEnrollment(int enrollmentId){ store.remove(enrollmentId); }
        @Override public java.util.Map<String,Integer> getWeights(int sectionId){ return new HashMap<>(weights.getOrDefault(sectionId, Collections.emptyMap())); }
        @Override public void saveWeights(int sectionId, java.util.Map<String,Integer> w){ weights.put(sectionId, new HashMap<>(w)); }
    }
}
