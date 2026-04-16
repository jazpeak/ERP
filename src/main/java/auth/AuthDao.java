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

    public java.util.List<User> getAllUsers() {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT user_id, username, role, password_hash, status FROM users_auth";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setStatus(rs.getString("status"));
                list.add(u);
            }
        } catch (SQLException e) {
            printError(e);
        }
        return list;
    }

    public int addUser(String username, String role, String rawPassword) {
        String sql = "INSERT INTO users_auth (username, role, password_hash, status) VALUES (?, ?, ?, 'active')";
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

    public boolean updatePasswordHash(int userId, String newHash) {
        String sql = "UPDATE users_auth SET password_hash=? WHERE user_id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

    public boolean setPasswordForUsername(String username, String rawPassword) {
        String sql = "UPDATE users_auth SET password_hash=? WHERE username=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

    public User verifyLogin(String username, String rawPassword) {
        User u = findByUsername(username);
        if (u == null)
            return null;
        return BCrypt.checkpw(rawPassword, u.getPasswordHash()) ? u : null;
    }

    public boolean setStatusForUsername(String username, String status) {
        String sql = "UPDATE users_auth SET status=? WHERE username=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

    public boolean insertUserRaw(int userId, String username, String role, String passwordHash, String status) {
        String sql = "INSERT INTO users_auth (user_id, username, role, password_hash, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, username);
            ps.setString(3, role);
            ps.setString(4, passwordHash);
            ps.setString(5, status);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            String up = "UPDATE users_auth SET username=?, role=?, password_hash=?, status=? WHERE user_id=?";
            try (Connection conn = dataSource.getConnection();
                    PreparedStatement ps = conn.prepareStatement(up)) {
                ps.setString(1, username);
                ps.setString(2, role);
                ps.setString(3, passwordHash);
                ps.setString(4, status);
                ps.setInt(5, userId);
                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                printError(ex);
                return false;
            }
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

}
