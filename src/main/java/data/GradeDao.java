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

    public void updateFinalGrade(int enrollmentId, String letter) {
        String sql = "UPDATE grades SET final_grade=? WHERE enroll_Id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, letter);
            ps.setInt(2, enrollmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            printError(e);
        }
    }

    public void deleteGradesForEnrollment(int enrollmentId) {
        String sql = "DELETE FROM grades WHERE enroll_Id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            printError(e);
        }
    }

    public java.util.Map<String, Integer> getWeights(int sectionId) {
        java.util.Map<String, Integer> weights = new java.util.HashMap<>();
        String sql = "SELECT component, weight FROM section_weights WHERE section_id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                weights.put(rs.getString("component"), rs.getInt("weight"));
            }
        } catch (SQLException e) {
            printError(e);
        }
        return weights;
    }

    public void saveWeights(int sectionId, java.util.Map<String, Integer> weights) {
        String delSql = "DELETE FROM section_weights WHERE section_id=?";
        String insSql = "INSERT INTO section_weights (section_id, component, weight) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psDel = conn.prepareStatement(delSql)) {
                psDel.setInt(1, sectionId);
                psDel.executeUpdate();
            }

            try (PreparedStatement psIns = conn.prepareStatement(insSql)) {
                for (var entry : weights.entrySet()) {
                    psIns.setInt(1, sectionId);
                    psIns.setString(2, entry.getKey());
                    psIns.setInt(3, entry.getValue());
                    psIns.addBatch();
                }
                psIns.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            printError(e);
        }
    }

}
