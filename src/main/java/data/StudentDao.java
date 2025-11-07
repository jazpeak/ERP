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
}
