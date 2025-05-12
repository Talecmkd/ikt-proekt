package finki.ikt.iktproekt.question.model;

import finki.ikt.iktproekt.base.BaseEntity;
import finki.ikt.iktproekt.question.model.enumeration.QuestionType;
import finki.ikt.iktproekt.quiz.model.Quiz;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    @Column(nullable = false, length = 500)
    private String questionText;

    @ElementCollection
    @CollectionTable(name = "question_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "answer", nullable = false)
    private List<String> answers;

    @Column(nullable = false)
    private String correctAnswer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    private Quiz quiz;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
