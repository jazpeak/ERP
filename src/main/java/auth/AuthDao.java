package auth;

import data.BaseDao;
import domain.User;
import javax.sql.DataSource;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

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

    public int addUser(String username, String role, String rawPassword) {
        String sql = "INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)";
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, hashed);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("[ERROR] Username already exists.");
            return -2;
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }

        return -1;
    }

}
