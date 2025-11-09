package data;

import domain.Course;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDao extends BaseDao {
    public CourseDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT code, title, credits FROM courses";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setCode(rs.getString("code"));
                c.setTitle(rs.getString("title"));
                c.setCredits(rs.getInt("credits"));
                list.add(c);
            }
        } catch (SQLException e) {
            printError(e);
        }
        return list;
    }

    public boolean exists(String code) {
        String sql = "SELECT 1 FROM courses WHERE code = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

    public boolean createCourse(String code, String title, int credits) {
        if (exists(code)) {
            System.out.println("Course already exists: " + code);
            return false;
        }
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, title);
            ps.setInt(3, credits);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

}
