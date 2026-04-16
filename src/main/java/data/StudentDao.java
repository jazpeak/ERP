package data;

import domain.Student;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDao extends BaseDao {

    public StudentDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT user_id, roll_no, prog, year FROM students";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student s = new Student();
                s.setUserId(rs.getInt("user_id"));
                s.setRollNo(rs.getString("roll_no"));
                s.setProg(rs.getString("prog"));
                s.setYear(rs.getInt("year"));
                list.add(s);
            }
        } catch (SQLException e) {
            printError(e);
        }
        return list;
    }

    public Student getByUserId(int userId) {
        String sql = "SELECT user_id, roll_no, prog, year FROM students WHERE user_id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Student s = new Student();
                s.setUserId(rs.getInt("user_id"));
                s.setRollNo(rs.getString("roll_no"));
                s.setProg(rs.getString("prog"));
                s.setYear(rs.getInt("year"));
                return s;
            }
        } catch (SQLException e) {
            printError(e);
        }
        return null;
    }

    public boolean addStudent(int userId, String rollNo, String program, int year) {
        String sql = "INSERT INTO students (user_id, roll_no, prog, year) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, rollNo);
            ps.setString(3, program);
            ps.setInt(4, year);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("========================================");
            System.err.println("[CRITICAL SQL ERROR] Failed to add student!");
            System.err.println("Message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            System.err.println("========================================");
            return false;
        }
    }

    public boolean existsByRoll(String rollNo) {
        String sql = "SELECT 1 FROM students WHERE roll_no=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

}
