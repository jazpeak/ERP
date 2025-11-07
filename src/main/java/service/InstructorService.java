package service;

import data.EnrollmentDao;
import data.GradeDao;
import data.SectionDao;
import domain.Enrollment;
import domain.Grade;
import domain.Section;
import domain.Student;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InstructorService {

    private final SectionDao sectionDao;
    private final EnrollmentDao enrollmentDao;
    private final GradeDao gradeDao;

    public InstructorService(SectionDao sectionDao, EnrollmentDao enrollmentDao, GradeDao gradeDao) {
        this.sectionDao = sectionDao;
        this.enrollmentDao = enrollmentDao;
        this.gradeDao = gradeDao;
    }


    public List<Section> getMySections(int instructorId) {
        return sectionDao.getSectionsByInstructor(instructorId);
    }

    public List<Student> getStudentsInSection(int sectionId) {
        return enrollmentDao.getStudentsBySection(sectionId);
    }

    public String enterScore(int enrollmentId, String component, double score) {
        try {
            gradeDao.UpdateGrade(enrollmentId, component, score);
            return "Score recorded for " + component;
        } catch (Exception e) {
            return "Failed to record score: " + e.getMessage();
        }
    }

    public Map<String, Double> getComponentStats(int sectionId, String component) {
        Map<String, Double> stats = new HashMap<>();
        List<Enrollment> enrollments = enrollmentDao.getBySection(sectionId);

        double total = 0, max = 0, min = 100;
        int count = 0;

        for (Enrollment e : enrollments) {
            Grade g = gradeDao.getGradeForComponent(e.getEnrollId(), component);
            if (g == null) continue;

            double score = g.getScore();
            total += score;
            max = Math.max(max, score);
            min = Math.min(min, score);
            count++;
        }

        if (count > 0) {
            stats.put("average", total / count);
            stats.put("highest", max);
            stats.put("lowest", min);
        }
        return stats;
    }

    public Map<String, Double> getSectionStats(int sectionId) {
        Map<String, Double> stats = new HashMap<>();
        List<Enrollment> enrollments = enrollmentDao.getBySection(sectionId);

        double total = 0, max = 0, min = 100;
        int count = 0;

        for (Enrollment e : enrollments) {
            List<Grade> grades = gradeDao.getGradesByEnrollment(e.getEnrollId());
            if (grades.isEmpty()) continue;

            double sum = grades.stream().mapToDouble(Grade::getScore).sum();

            total += sum;
            max = Math.max(max, sum);
            min = Math.min(min, sum);
            count++;
        }

        if (count > 0) {
            stats.put("average", total / count);
            stats.put("highest", max);
            stats.put("lowest", min);
        }

        return stats;
    }


    private String mapScoreToLetter(double score) {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        if (score >= 50) return "D";
        return "F";
    }
}
