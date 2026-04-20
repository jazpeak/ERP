package data;

import domain.Enrollment;
import domain.Student;
import domain.Section;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDao extends BaseDao {

    public EnrollmentDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT enroll_id, student_id, section_id, status FROM enrollments";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setEnrollId(rs.getInt("enroll_id"));
                e.setStudentId(rs.getInt("student_id"));
                e.setSectionId(rs.getInt("section_id"));
                e.setStatus(rs.getString("status"));
                list.add(e);
            }
        } catch (SQLException ex) {
            printError(ex);
        }
        return list;
    }

    public boolean enrollStudent(int studentId, int sectionId) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'enrolled')";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("duplicate")) {
                // Handle re-enrollment: Update existing record to 'enrolled'
                String updateSql = "UPDATE enrollments SET status='enrolled' WHERE student_id=? AND section_id=?";
                try (Connection conn = dataSource.getConnection();
                        PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, sectionId);
                    return ps.executeUpdate() > 0;
                } catch (SQLException e) {
                    printError(e);
                }
            } else {
                printError(ex);
            }
            return false;
        }
    }

    public boolean dropStudent(int studentId, int sectionId) {
        String sql = "UPDATE enrollments SET status='dropped' WHERE student_id=? AND section_id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            printError(ex);
            return false;
        }
    }

    public Integer getEnrollmentId(int studentId, int sectionId) {
        String sql = "SELECT enroll_id FROM enrollments WHERE student_id=? AND section_id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            printError(ex);
        }
        return null;
    }

    public boolean deleteEnrollmentById(int enrollId) {
        String sql = "DELETE FROM enrollments WHERE enroll_id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            printError(ex);
            return false;
        }
    }

    public int countEnrolled(int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE section_id=? AND status='enrolled'";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException ex) {
            printError(ex);
        }
        return 0;
    }

    public boolean isEnrolled(int studentId, int sectionId) {
        String sql = "SELECT 1 FROM enrollments WHERE student_id=? AND section_id=? AND status='enrolled'";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            printError(ex);
            return false;
        }
    }

    public List<Enrollment> getBySection(int sectionId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT enroll_id, student_id, section_id, status FROM enrollments where section_id=? AND status='enrolled'";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setEnrollId(rs.getInt("enroll_id"));
                e.setStudentId(rs.getInt("student_id"));
                e.setSectionId(rs.getInt("section_id"));
                e.setStatus(rs.getString("status"));
                list.add(e);
            }
        } catch (SQLException ex) {
            printError(ex);
        }
        return list;
    }

    public List<Enrollment> getByStudent(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT enroll_id, student_id, section_id, status FROM enrollments WHERE student_id=? AND status='enrolled'";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setEnrollId(rs.getInt("enroll_id"));
                e.setStudentId(rs.getInt("student_id"));
                e.setSectionId(rs.getInt("section_id"));
                e.setStatus(rs.getString("status"));
                list.add(e);
            }
        } catch (SQLException ex) {
            printError(ex);
        }
        return list;
    }

    public List<Section> getEnrolledSectionsByStudent(int studentId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.section_id, s.course_code, s.inst_id, s.day, s.time_slot, s.room, s.cap, s.seats_left, s.sem, s.year, s.reg_deadline "
                +
                "FROM sections s JOIN enrollments e ON s.section_id = e.section_id " +
                "WHERE e.student_id=? AND e.status='enrolled'";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Section s = new Section();
                s.setSectionId(rs.getInt("section_id"));
                s.setCourseCode(rs.getString("course_code"));
                s.setInstructorId(rs.getInt("inst_id"));
                s.setDay(rs.getString("day"));
                s.setTimeSlot(rs.getString("time_slot"));
                s.setRoom(rs.getString("room"));
                s.setCapacity(rs.getInt("cap"));
                s.setSeatsLeft(rs.getInt("seats_left"));
                s.setSemester(rs.getString("sem"));
                s.setYear(rs.getInt("year"));
                s.setRegDeadline(rs.getDate("reg_deadline"));
                sections.add(s);
            }
        } catch (SQLException ex) {
            printError(ex);
        }
        return sections;
    }

    public List<Student> getStudentsBySection(int sectionId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.user_id, s.roll_no, s.prog, s.year FROM students s JOIN enrollments e ON s.user_id = e.student_id WHERE e.section_id = ? AND e.status = 'enrolled'";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Student s = new Student();
                s.setUserId(rs.getInt("user_id"));
                s.setRollNo(rs.getString("roll_no"));
                s.setProg(rs.getString("prog"));
                s.setYear(rs.getInt("year"));
                students.add(s);
            }
        } catch (SQLException e) {
            printError(e);
        }
        return students;
    }

    public int getTotalEnrollments() {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE status='enrolled'";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException ex) {
            printError(ex);
        }
        return 0;
    }
}
