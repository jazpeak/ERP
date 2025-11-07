package data;

import domain.Instructor;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDao extends BaseDao {

    public InstructorDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Instructor> getAllInstructors() {
        List<Instructor> list = new ArrayList<>();
        String sql = "SELECT user_id, dept FROM instructors";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Instructor i = new Instructor();
                i.setUserId(rs.getInt("user_id"));
                i.setDept(rs.getString("dept"));
                list.add(i);
            }
        } catch (SQLException e) {
            printError(e);
        }
        return list;
    }

    public Instructor getByUserId(int userId) {
        String sql = "SELECT * FROM instructors WHERE user_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Instructor i = new Instructor();
                i.setUserId(rs.getInt("user_id"));
                i.setDept(rs.getString("dept"));
                return i;
            }
        } catch (SQLException e) {
            printError(e);
        }
        return null;
    }
}
