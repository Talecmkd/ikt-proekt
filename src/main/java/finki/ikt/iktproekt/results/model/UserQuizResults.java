package finki.ikt.iktproekt.results.model;

import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_quiz_results")
@Data
@NoArgsConstructor
public class UserQuizResults {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long result_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private double scorePercentage;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @ElementCollection
    @CollectionTable(name = "user_quiz_feedback", joinColumns = @JoinColumn(name = "result_id"))
    @Column(name = "feedback", nullable = false)
    private List<String> feedback;

    @Column(nullable = false)
    private long timeTakenMillis;

    public UserQuizResults(User user, Quiz quiz, double scorePercentage, List<String> feedback, LocalDateTime submittedAt, long timeTakenMillis) {
        this.user = user;
        this.quiz = quiz;
        this.scorePercentage = scorePercentage;
        this.submittedAt = submittedAt;
        this.feedback = feedback;
        this.timeTakenMillis = timeTakenMillis;
    }
}
