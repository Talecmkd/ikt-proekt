package finki.ikt.iktproekt.model;

import finki.ikt.iktproekt.base.BaseEntity;
import finki.ikt.iktproekt.model.enumeration.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_quizzy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    public Role role;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}