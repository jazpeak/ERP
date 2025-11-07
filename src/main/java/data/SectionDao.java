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
        String sql = "SELECT section_id, course_code, inst_id, day, time_slot, room, cap, sem, year FROM sections";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Section s = new Section();
                s.setSectionId(rs.getInt("section_id"));
                s.setCourseCode(rs.getString("course_code"));
                s.setInstructorId(rs.getInt("inst_id"));
                s.setDay(rs.getString("day"));
                s.setTimeSlot(rs.getString("time_slot"));
                s.setRoom(rs.getString("room"));
                s.setCapacity(rs.getInt("cap"));
                s.setSemester(rs.getString("sem"));
                s.setYear(rs.getInt("year"));
                sections.add(s);
            }
        } catch (SQLException e) {
            printError(e);
        }
        return sections;
    }

}