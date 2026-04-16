package service;

import data.EnrollmentDao;
import data.GradeDao;
import data.SectionDao;
import data.SettingDao;
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
    private final SettingDao settingDao;

    public InstructorService(SectionDao sectionDao, EnrollmentDao enrollmentDao, GradeDao gradeDao,
            SettingDao settingDao) {
        this.sectionDao = sectionDao;
        this.enrollmentDao = enrollmentDao;
        this.gradeDao = gradeDao;
        this.settingDao = settingDao;
    }

    public List<Section> getMySections(int instructorId) {
        return sectionDao.getSectionsByInstructor(instructorId);
    }

    public List<Student> getStudentsInSection(int sectionId) {
        return enrollmentDao.getStudentsBySection(sectionId);
    }

    public List<Enrollment> listEnrollments(int sectionId) {
        return enrollmentDao.getBySection(sectionId);
    }

    public String enterScore(int enrollmentId, String component, double score) {
        if (settingDao.isMaintenanceOn())
            return "Maintenance mode ON: cannot enter grades";
        try {
            gradeDao.UpdateGrade(enrollmentId, component, score);
            return "Score recorded";
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
            if (g == null)
                continue;

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
        Map<String, Integer> weights = gradeDao.getWeights(sectionId);
        if (weights.isEmpty()) {
            weights.put("Quiz", 20);
            weights.put("Midsem", 30);
            weights.put("Endsem", 50);
        }

        double total = 0, max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
        int count = 0;

        for (Enrollment e : enrollments) {
            double finalScore = 0.0;
            int totalWeight = 0;
            for (var entry : weights.entrySet()) {
                Grade g = gradeDao.getGradeForComponent(e.getEnrollId(), entry.getKey());
                if (g == null) continue;
                finalScore += g.getScore() * entry.getValue() / 100.0;
                totalWeight += entry.getValue();
            }
            if (totalWeight == 0) continue;
            total += finalScore;
            max = Math.max(max, finalScore);
            min = Math.min(min, finalScore);
            count++;
        }

        if (count > 0) {
            stats.put("average", total / count);
            stats.put("highest", max);
            stats.put("lowest", min);
        }

        return stats;
    }

    public void computeFinalGrades(int sectionId, Map<String, Integer> weights) {
        if (settingDao.isMaintenanceOn()) return;
        List<Enrollment> enrollments = enrollmentDao.getBySection(sectionId);
        for (Enrollment e : enrollments) {
            double total = 0;
            int weightSum = 0;
            for (var entry : weights.entrySet()) {
                Grade g = gradeDao.getGradeForComponent(e.getEnrollId(), entry.getKey());
                if (g == null)
                    continue;
                total += g.getScore() * entry.getValue();
                weightSum += entry.getValue();
            }
            if (weightSum == 0)
                continue;
            double pct = total / weightSum;
            String letter = mapScoreToLetter(pct);
            gradeDao.updateFinalGrade(e.getEnrollId(), letter);
        }
    }

    public byte[] exportGradesCsv(int sectionId) {
        StringBuilder sb = new StringBuilder();
        sb.append("EnrollId,Component,Score,Final\n");
        List<Enrollment> enrollments = enrollmentDao.getBySection(sectionId);
        for (Enrollment e : enrollments) {
            List<Grade> grades = gradeDao.getGradesByEnrollment(e.getEnrollId());
            if (grades.isEmpty()) {
                sb.append(e.getEnrollId()).append(",,,").append("\n");
            } else {
                for (Grade g : grades) {
                    sb.append(e.getEnrollId()).append(",")
                            .append(g.getComponent()).append(",")
                            .append(g.getScore()).append(",")
                            .append(g.getFinalGrade() == null ? "" : g.getFinalGrade())
                            .append("\n");
                }
            }
        }
        return sb.toString().getBytes();
    }

    public String importGradesCsv(int sectionId, java.io.File file) {
        if (settingDao.isMaintenanceOn())
            return "Maintenance mode ON: cannot import grades";
        try (java.io.Reader r = new java.io.FileReader(file)) {
            var csv = org.apache.commons.csv.CSVFormat.DEFAULT.builder().setSkipHeaderRecord(true).setHeader().build()
                    .parse(r);
            for (var rec : csv) {
                int enrollId = Integer.parseInt(rec.get("EnrollId"));
                String comp = rec.get("Component");
                double score = Double.parseDouble(rec.get("Score"));
                gradeDao.UpdateGrade(enrollId, comp, score);
            }
            return "Grades imported";
        } catch (Exception e) {
            return "Failed to import: " + e.getMessage();
        }
    }

    private String mapScoreToLetter(double score) {
        if (score >= 90)
            return "A+";
        if (score >= 80)
            return "A";
        if (score >= 70)
            return "B";
        if (score >= 60)
            return "C";
        if (score >= 50)
            return "D";
        return "F";
    }

    public void setSectionNotice(int sectionId, String text) {
        settingDao.updateSetting("sec_notice_" + sectionId, text == null ? "" : text);
    }

    public String getSectionNotice(int sectionId) {
        var s = settingDao.getSetting("sec_notice_" + sectionId);
        return s != null ? s.getValue() : "";
    }

    public List<Enrollment> getSectionEnrollments(int sectionId) {
        return enrollmentDao.getBySection(sectionId);
    }

    public String enterGrade(int enrollmentId, String component, int score, String finalGrade) {
        return enterGrade(enrollmentId, component, (double) score, finalGrade);
    }

    public String enterGrade(int enrollmentId, String component, double score, String finalGrade) {
        if (settingDao.isMaintenanceOn())
            return "Maintenance mode ON: cannot enter grades";
        try {
            gradeDao.UpdateGrade(enrollmentId, component, score);
            if (finalGrade != null && !finalGrade.trim().isEmpty()) {
                gradeDao.updateFinalGrade(enrollmentId, finalGrade.trim());
            }
            return "Grade saved successfully";
        } catch (Exception e) {
            return "Failed to save grade: " + e.getMessage();
        }
    }

    public String computeFinalGrades(int sectionId) {
        if (settingDao.isMaintenanceOn())
            return "Maintenance mode ON: cannot compute final grades";
        Map<String, Integer> weights = gradeDao.getWeights(sectionId);
        if (weights.isEmpty()) {
            weights.put("Quiz", 20);
            weights.put("Midsem", 30);
            weights.put("Endsem", 50);
            computeFinalGrades(sectionId, weights);
            return "Final grades computed using DEFAULT weights (Quiz:20%, Midsem:30%, Endsem:50%). Configure custom weights to change this.";
        }
        computeFinalGrades(sectionId, weights);
        return "Final grades computed using CUSTOM weights.";
    }

    public void saveGradingWeights(int sectionId, Map<String, Integer> weights) {
        if (settingDao.isMaintenanceOn()) return;
        gradeDao.saveWeights(sectionId, weights);
    }

    public Map<String, Integer> getGradingWeights(int sectionId) {
        return gradeDao.getWeights(sectionId);
    }

    public String getClassStats(int sectionId) {
        List<Enrollment> enrollments = enrollmentDao.getBySection(sectionId);
        if (enrollments.isEmpty()) {
            return "No enrollments found for this section.";
        }

        Map<String, Double> stats = getSectionStats(sectionId);
        StringBuilder sb = new StringBuilder();
        sb.append("Class Statistics for Section ").append(sectionId).append(":\n\n");
        sb.append("Total Students: ").append(enrollments.size()).append("\n");
        if (!stats.isEmpty()) {
            sb.append(String.format("Average Score: %.2f\n", stats.getOrDefault("average", 0.0)));
            sb.append(String.format("Highest Score: %.2f\n", stats.getOrDefault("highest", 0.0)));
            sb.append(String.format("Lowest Score: %.2f\n", stats.getOrDefault("lowest", 0.0)));
            Map<String, Integer> weights = gradeDao.getWeights(sectionId);
            if (weights.isEmpty()) {
                weights.put("Quiz", 20);
                weights.put("Midsem", 30);
                weights.put("Endsem", 50);
            }
            sb.append("\nComponent Stats:\n");
            for (String comp : weights.keySet()) {
                Map<String, Double> cs = getComponentStats(sectionId, comp);
                if (!cs.isEmpty()) {
                    sb.append(String.format("- %s: avg %.2f, high %.2f, low %.2f\n",
                            comp,
                            cs.getOrDefault("average", 0.0),
                            cs.getOrDefault("highest", 0.0),
                            cs.getOrDefault("lowest", 0.0)));
                }
            }
        } else {
            sb.append("No grades entered yet.\n");
        }
        return sb.toString();
    }

    public String enterGrade(int enrollmentId, String component, double score) {
        return enterGrade(enrollmentId, component, score, null);
    }

    public Map<Integer, Map<String, Object>> getStudentGrades(int sectionId) {
        Map<Integer, Map<String, Object>> result = new HashMap<>();
        List<Enrollment> enrollments = enrollmentDao.getBySection(sectionId);

        for (Enrollment e : enrollments) {
            Map<String, Object> grades = new HashMap<>();
            List<Grade> gradeList = gradeDao.getGradesByEnrollment(e.getEnrollId());
            for (Grade g : gradeList) {
                grades.put(g.getComponent(), g.getScore());
                if (g.getFinalGrade() != null) {
                    grades.put("Final Grade", g.getFinalGrade());
                }
            }
            result.put(e.getEnrollId(), grades);
        }
        return result;
    }
}
