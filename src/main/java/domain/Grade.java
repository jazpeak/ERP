package domain;

public class Grade {
    private int enroll_Id;
    private String component;
    private double score;
    private String finalGrade;

    public int getEnrollmentId() { return enroll_Id; }
    public void setEnrollmentId(int enrollmentId) { this.enroll_Id = enroll_Id; }

    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getFinalGrade() { return finalGrade; }
    public void setFinalGrade(String finalGrade) { this.finalGrade = finalGrade; }

    @Override
    public String toString() {
        return String.format("%s: %.2f (%s)", component, score, finalGrade == null ? "-" : finalGrade);
    }
}
