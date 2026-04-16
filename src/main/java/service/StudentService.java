// src/main/java/service/StudentService.java
package service;

import data.CourseDao;
import data.SectionDao;
import data.EnrollmentDao;
import data.GradeDao;
import data.SettingDao;
import data.InstructorDao;
import domain.Course;
import domain.Section;
import domain.Enrollment;
import domain.Grade;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class StudentService {
    private final CourseDao courseDao;
    private final SectionDao sectionDao;
    private final EnrollmentDao enrollmentDao;
    private final GradeDao gradeDao;
    private final SettingDao settingDao;
    private final InstructorDao instructorDao;

    public StudentService(CourseDao courseDao, SectionDao sectionDao, EnrollmentDao enrollmentDao, GradeDao gradeDao,
            SettingDao settingDao, InstructorDao instructorDao) {
        this.courseDao = courseDao;
        this.sectionDao = sectionDao;
        this.enrollmentDao = enrollmentDao;
        this.gradeDao = gradeDao;
        this.settingDao = settingDao;
        this.instructorDao = instructorDao;
    }

    public boolean isMaintenanceOn() {
        return settingDao.isMaintenanceOn();
    }

    public List<Course> browseCatalogCourses() {
        return courseDao.getAllCourses();
    }

    public List<Section> browseCatalogSections() {
        return sectionDao.getAllSections();
    }

    public List<Object[]> browseCatalogWithDetails() {
        List<Object[]> result = new ArrayList<>();
        List<Section> sections = sectionDao.getAllSections();

        for (Section section : sections) {
            Course course = courseDao.getCourseByCode(section.getCourseCode());
            if (course == null)
                continue;

            String instructorName = "N/A";
            if (section.getInstructorId() > 0) {
                String name = instructorDao.getNameByUserId(section.getInstructorId());
                if (name != null) {
                    instructorName = name;
                }
            }

            // Return: sectionId (hidden, for registration), code, title, credits, capacity,
            // instructor
            result.add(new Object[] {
                    section.getSectionId(),
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits(),
                    section.getSeatsLeft() + "/" + section.getCapacity(),
                    instructorName,
                    section.getRegDeadline() != null ? section.getRegDeadline().toString() : "None"
            });
        }

        return result;
    }

    public String registerSection(int studentId, int sectionId) {
        if (isMaintenanceOn())
            return "Maintenance ON: cannot register";
        if (enrollmentDao.isEnrolled(studentId, sectionId))
            return "Already registered";
        Section s = sectionDao.getById(sectionId);
        if (s == null)
            return "Section not found";
        int taken = enrollmentDao.countEnrolled(sectionId);
        if (taken >= s.getCapacity())
            return "Section full";
        if (s.getRegDeadline() != null) {
            LocalDate deadline = s.getRegDeadline().toLocalDate();
            if (LocalDate.now().isAfter(deadline)) {
                return "Registration deadline passed";
            }
        }
        if (s.getSeatsLeft() <= 0)
            return "No seats left";
        boolean ok = enrollmentDao.enrollStudent(studentId, sectionId);
        if (ok) {
            sectionDao.updateSeats(sectionId, -1);
        }
        return ok ? "Registered" : "Failed to register";
    }

    public String dropSection(int studentId, int sectionId) {
        if (isMaintenanceOn())
            return "Maintenance ON: cannot drop";
        LocalDateTime now = LocalDateTime.now();
        String val = settingDao.getSetting("drop_deadline") != null ? settingDao.getSetting("drop_deadline").getValue()
                : null;
        if (val != null) {
            LocalDateTime deadline = parseDeadline(val);
            if (deadline != null && now.isAfter(deadline))
                return "Drop deadline passed";
        }
        Integer enrollId = enrollmentDao.getEnrollmentId(studentId, sectionId);
        if (enrollId == null)
            return "Not enrolled";
        gradeDao.deleteGradesForEnrollment(enrollId);
        boolean ok = enrollmentDao.deleteEnrollmentById(enrollId);
        if (ok) sectionDao.updateSeats(sectionId, 1);
        return ok ? "Dropped" : "Failed to drop";
    }

    public List<Section> timetable(int studentId) {
        return enrollmentDao.getEnrolledSectionsByStudent(studentId);
    }

    public Map<Integer, List<Grade>> viewGrades(int studentId) {
        Map<Integer, List<Grade>> map = new LinkedHashMap<>();
        for (Enrollment e : enrollmentDao.getByStudent(studentId)) {
            List<Grade> gs = gradeDao.getGradesByEnrollment(e.getEnrollId());
            map.put(e.getEnrollId(), gs);
        }
        return map;
    }

    public List<Object[]> gradesTableData(int studentId) {
        List<Object[]> rows = new ArrayList<>();
        for (Enrollment e : enrollmentDao.getByStudent(studentId)) {
            Section s = sectionDao.getById(e.getSectionId());
            List<Grade> gs = gradeDao.getGradesByEnrollment(e.getEnrollId());
            if (gs.isEmpty()) {
                rows.add(new Object[] { e.getSectionId(), s != null ? s.getCourseCode() : "", "", "", "" });
            } else {
                for (Grade g : gs) {
                    rows.add(new Object[] { e.getSectionId(), s != null ? s.getCourseCode() : "", g.getComponent(),
                            g.getScore(), g.getFinalGrade() });
                }
            }
        }
        return rows;
    }

    public List<Object[]> gradesPivotTableData(int studentId) {
        List<Object[]> rows = new ArrayList<>();
        for (Enrollment e : enrollmentDao.getByStudent(studentId)) {
            Section s = sectionDao.getById(e.getSectionId());
            double endsem = 0.0, midsem = 0.0, quiz = 0.0;
            String letter = "";
            for (Grade g : gradeDao.getGradesByEnrollment(e.getEnrollId())) {
                String comp = g.getComponent() != null ? g.getComponent() : "";
                if (comp.equalsIgnoreCase("Endsem")) endsem = g.getScore();
                else if (comp.equalsIgnoreCase("Midsem")) midsem = g.getScore();
                else if (comp.equalsIgnoreCase("Quiz")) quiz = g.getScore();
                if (g.getFinalGrade() != null && !g.getFinalGrade().isBlank()) letter = g.getFinalGrade();
            }
            rows.add(new Object[] { e.getSectionId(), s != null ? s.getCourseCode() : "", endsem, midsem, quiz, letter });
        }
        return rows;
    }

    public byte[] exportTranscriptCsv(int studentId) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.builder().setHeader("Course", "Section", "Component", "Score", "Final").build())) {
            List<Enrollment> enrollments = enrollmentDao.getByStudent(studentId);
            for (Enrollment e : enrollments) {
                Section s = sectionDao.getById(e.getSectionId());
                List<Grade> grades = gradeDao.getGradesByEnrollment(e.getEnrollId());
                if (grades.isEmpty()) {
                    printer.printRecord(s != null ? s.getCourseCode() : "", e.getSectionId(), "", "", "");
                } else {
                    for (Grade g : grades) {
                        printer.printRecord(s != null ? s.getCourseCode() : "", e.getSectionId(), g.getComponent(),
                                g.getScore(), g.getFinalGrade());
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return out.toByteArray();
    }

    private LocalDateTime parseDeadline(String v) {
        try {
            if (v.length() == 10)
                return LocalDate.parse(v, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);
            return LocalDateTime.parse(v, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }

    public java.util.List<String> getSectionNotices(int studentId) {
        java.util.List<String> list = new java.util.ArrayList<>();
        for (Enrollment e : enrollmentDao.getByStudent(studentId)) {
            int secId = e.getSectionId();
            var s = settingDao.getSetting("sec_notice_" + secId);
            if (s != null && s.getValue() != null && !s.getValue().isBlank()) {
                String hash = Integer.toString(s.getValue().hashCode());
                var seen = settingDao.getSetting("sec_notice_read_" + studentId + "_" + secId);
                if (hash.equals(seen != null ? seen.getValue() : ""))
                    continue; // skip seen
                Section sec = sectionDao.getById(secId);
                String label = (sec != null ? sec.getCourseCode() : "") + " #" + secId;
                list.add(label + ":\n" + s.getValue());
            }
        }
        return list;
    }

    public boolean hasUnreadAdminNotice(int userId) {
        var n = settingDao.getSetting("notice");
        String txt = n != null ? n.getValue() : "";
        String hash = Integer.toString(txt.hashCode());
        var seen = settingDao.getSetting("notice_read_" + userId);
        return !hash.equals(seen != null ? seen.getValue() : "");
    }

    public void markAdminNoticeRead(int userId) {
        var n = settingDao.getSetting("notice");
        String txt = n != null ? n.getValue() : "";
        String hash = Integer.toString(txt.hashCode());
        settingDao.updateSetting("notice_read_" + userId, hash);
    }

    public java.util.List<Integer> getUnreadSectionIds(int userId) {
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        for (Enrollment e : enrollmentDao.getByStudent(userId)) {
            int secId = e.getSectionId();
            var s = settingDao.getSetting("sec_notice_" + secId);
            String txt = s != null ? s.getValue() : null;
            if (txt == null || txt.isBlank())
                continue;
            String hash = Integer.toString(txt.hashCode());
            var seen = settingDao.getSetting("sec_notice_read_" + userId + "_" + secId);
            if (!hash.equals(seen != null ? seen.getValue() : ""))
                ids.add(secId);
        }
        return ids;
    }

    public void markSectionNoticeRead(int userId, int sectionId) {
        var s = settingDao.getSetting("sec_notice_" + sectionId);
        String txt = s != null ? s.getValue() : "";
        String hash = Integer.toString(txt.hashCode());
        settingDao.updateSetting("sec_notice_read_" + userId + "_" + sectionId, hash);
    }

    public List<Object[]> getRegistrationsWithDetails(int studentId) {
        List<Object[]> result = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentDao.getByStudent(studentId);

        for (Enrollment e : enrollments) {
            if (!"enrolled".equalsIgnoreCase(e.getStatus()))
                continue;

            Section section = sectionDao.getById(e.getSectionId());
            if (section == null)
                continue;

            Course course = courseDao.getCourseByCode(section.getCourseCode());
            String instructorName = "N/A";
            if (section.getInstructorId() > 0) {
                String name = instructorDao.getNameByUserId(section.getInstructorId());
                if (name != null)
                    instructorName = name;
            }

            result.add(new Object[] {
                    section.getSectionId(),
                    course != null ? course.getCode() : "",
                    course != null ? course.getTitle() : "",
                    course != null ? course.getCredits() : 0,
                    instructorName,
                    section.getDay() + " " + section.getTimeSlot() + " (" + section.getRoom() + ")"
            });
        }
        return result;
    }
}
