package data;

import domain.Grade;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDao extends BaseDao {

    public GradeDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Grade> getGradesByEnrollment(int enroll_Id) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE enroll_Id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enroll_Id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Grade g = new Grade();
                g.setEnrollmentId(rs.getInt("enroll_Id"));
                g.setComponent(rs.getString("component"));
                g.setScore(rs.getDouble("score"));
                g.setFinalGrade(rs.getString("final_grade"));
                list.add(g);
            }
        } catch (SQLException e) {
            printError(e);
        }
        return list;
    }

    public void UpdateGrade(int enrollmentId, String component, double score) {
        String sql = "INSERT INTO grades (enroll_Id, component, score) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE score = VALUES(score)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setString(2, component);
            ps.setDouble(3, score);
            ps.executeUpdate();
        } catch (SQLException e) {
            printError(e);
        }
    }

    public Grade getGradeForComponent(int enrollmentId, String component) {
        String sql = "SELECT enroll_Id, component, score FROM grades WHERE enroll_Id=? AND component=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setString(2, component);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Grade g = new Grade();
                g.setEnrollmentId(rs.getInt("enroll_Id"));
                g.setComponent(rs.getString("component"));
                g.setScore(rs.getDouble("score"));
                return g;
            }
        } catch (SQLException e) {
            printError(e);
        }
        return null;
    }


}
