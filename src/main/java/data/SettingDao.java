package data;

import domain.Setting;
import javax.sql.DataSource;
import java.sql.*;

public class SettingDao extends BaseDao {

    public SettingDao(DataSource dataSource) {
        super(dataSource);
    }

    public Setting getSetting(String key) {
        String sql = "SELECT * FROM settings WHERE `key`=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Setting s = new Setting();
                s.setKey(rs.getString("key"));
                s.setValue(rs.getString("value"));
                return s;
            }
        } catch (SQLException e) {
            printError(e);
        }
        return null;
    }

    public void updateSetting(String key, String value) {
        String sql = "UPDATE settings SET value=? WHERE `key`=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setString(2, key);
            ps.executeUpdate();
        } catch (SQLException e) {
            printError(e);
        }
    }

    public void setMaintenance(boolean on) {
        String sql = "UPDATE settings SET value=? WHERE `key`='maintenance'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, on ? "on" : "off");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    public boolean isMaintenanceOn() {
        String sql = "SELECT value FROM settings WHERE `key`='maintenance'";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return "on".equalsIgnoreCase(rs.getString("value"));
            }
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
        return false;
    }
}
