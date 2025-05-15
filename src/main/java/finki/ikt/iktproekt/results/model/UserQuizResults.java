package finki.ikt.iktproekt.results.model;

import finki.ikt.iktproekt.base.BaseEntity;
import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.user.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserQuizResults extends BaseEntity {

    @ManyToOne
    private User user;

    @ManyToOne
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
}
