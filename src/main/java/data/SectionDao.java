package data;

import domain.Section;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDao extends BaseDao {

    public SectionDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Section> getAllSections() {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT section_id, course_code, inst_id, day, time_slot, room, cap, seats_left, sem, year, reg_deadline FROM sections";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sections.add(map(rs));
            }
        } catch (SQLException e) {
            printError(e);
        }
        return sections;
    }

    public List<Section> getSectionsByInstructor(int instructorId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT section_id, course_code, inst_id, day, time_slot, room, cap, seats_left, sem, year, reg_deadline FROM sections where inst_id=?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sections.add(map(rs));
            }
        } catch (SQLException e) {
            printError(e);
        }
        return sections;
    }

    public boolean assignInstructor(int sectionId, int instructorId) {
        String sql = "UPDATE sections SET inst_id = ? WHERE section_id = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ps.setInt(2, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

    public boolean updateSeats(int sectionId, int delta) {
        String sql = "UPDATE sections SET seats_left = seats_left + ? WHERE section_id = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

    public int createSection(String courseCode, Integer instructorId, String day, String timeSlot, String room,
            int capacity, String semester, int year, java.sql.Date regDeadline) {
        int nextId = getNextId();
        String sql = "INSERT INTO sections (section_id, course_code, inst_id, day, time_slot, room, cap, seats_left, sem, year, reg_deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nextId);
            ps.setString(2, courseCode);
            if (instructorId == null)
                ps.setNull(3, Types.INTEGER);
            else
                ps.setInt(3, instructorId);
            ps.setString(4, day);
            ps.setString(5, timeSlot);
            ps.setString(6, room);
            ps.setInt(7, capacity);
            ps.setInt(8, capacity); // Initialize seats_left to capacity
            ps.setString(9, semester);
            ps.setInt(10, year);
            ps.setDate(11, regDeadline);

            int rows = ps.executeUpdate();
            if (rows > 0)
                return nextId;

        } catch (SQLException e) {
            System.err.println("========================================");
            System.err.println("[CRITICAL SQL ERROR] Failed to create section!");
            System.err.println("Message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            System.err.println("========================================");
        }
        return -1;
    }

    private int getNextId() {
        String sql = "SELECT MAX(section_id) FROM sections";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            printError(e);
        }
        return 1;
    }

    public Section getById(int sectionId) {
        String sql = "SELECT section_id, course_code, inst_id, day, time_slot, room, cap, seats_left, sem, year, reg_deadline FROM sections WHERE section_id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs);
        } catch (SQLException e) {
            printError(e);
        }
        return null;
    }

    public boolean clearInstructor(int sectionId) {
        String sql = "UPDATE sections SET inst_id=NULL WHERE section_id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

    public boolean deleteSection(int sectionId) {
        String sql = "DELETE FROM sections WHERE section_id=?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            printError(e);
            return false;
        }
    }

    private Section map(ResultSet rs) throws SQLException {
        Section s = new Section();
        s.setSectionId(rs.getInt("section_id"));
        s.setCourseCode(rs.getString("course_code"));
        s.setInstructorId(rs.getInt("inst_id"));
        s.setDay(rs.getString("day"));
        s.setTimeSlot(rs.getString("time_slot"));
        s.setRoom(rs.getString("room"));
        s.setCapacity(rs.getInt("cap"));
        s.setSeatsLeft(rs.getInt("seats_left"));
        s.setSemester(rs.getString("sem"));
        s.setYear(rs.getInt("year"));
        s.setRegDeadline(rs.getDate("reg_deadline"));
        return s;
    }

}
