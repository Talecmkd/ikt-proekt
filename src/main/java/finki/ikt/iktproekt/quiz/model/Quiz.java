package finki.ikt.iktproekt.quiz.model;

import finki.ikt.iktproekt.base.BaseEntity;

import finki.ikt.iktproekt.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

    @Column(columnDefinition = "text")
    private String pdfText;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
