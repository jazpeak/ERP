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
        String upd = "UPDATE settings SET value=? WHERE `key`=?";
        String ins = "INSERT INTO settings(`key`, value) VALUES(?, ?)";
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(upd)) {
                ps.setString(1, value);
                ps.setString(2, key);
                int n = ps.executeUpdate();
                if (n == 0) {
                    try (PreparedStatement ip = conn.prepareStatement(ins)) {
                        ip.setString(1, key);
                        ip.setString(2, value);
                        ip.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            printError(e);
        }
    }

    public void setMaintenance(boolean on) {
        String upd = "UPDATE settings SET value=? WHERE `key`='maintenance'";
        String ins = "INSERT INTO settings(`key`, value) VALUES('maintenance', ?)";
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(upd)) {
                ps.setString(1, on ? "on" : "off");
                int n = ps.executeUpdate();
                if (n == 0) {
                    try (PreparedStatement insPs = conn.prepareStatement(ins)) {
                        insPs.setString(1, on ? "on" : "off");
                        insPs.executeUpdate();
                    }
                }
            }
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
