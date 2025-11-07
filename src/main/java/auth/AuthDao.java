package auth;

import data.BaseDao;
import domain.User;
import javax.sql.DataSource;
import java.sql.*;

public class AuthDao extends BaseDao {

    public AuthDao(DataSource dataSource) {
        super(dataSource);
    }

    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, role, password_hash, status FROM users_auth WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setStatus(rs.getString("status"));
                return u;
            }
        } catch (SQLException e) {
            printError(e);
        }
        return null;
    }
}
