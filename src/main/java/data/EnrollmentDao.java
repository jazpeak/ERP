package data;

import domain.Enrollment;
import domain.Student;
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
            if (ex.getMessage().contains("Duplicate entry")) {
                System.out.println("️Student already enrolled in this section.");
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

    public List<Enrollment> getBySection(int sectionId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT enroll_id, student_id, section_id, status FROM enrollments where section_id=?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
             ps.setInt(1,sectionId);
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




}
