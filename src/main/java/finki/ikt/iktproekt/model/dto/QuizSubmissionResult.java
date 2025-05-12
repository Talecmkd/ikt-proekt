package finki.ikt.iktproekt.model.dto;

import java.util.List;

public class QuizSubmissionResult {
    private double scorePercentage;
    private List<String> feedback;

    public QuizSubmissionResult(double scorePercentage, List<String> feedback) {
        this.scorePercentage = scorePercentage;
        this.feedback = feedback;
    }

    public double getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(double scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<String> feedback) {
        this.feedback = feedback;
    }
}
