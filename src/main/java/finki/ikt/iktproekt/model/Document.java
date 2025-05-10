package finki.ikt.iktproekt.model;

import finki.ikt.iktproekt.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Document extends BaseEntity {

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private boolean processed = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private Quiz quiz;

    @PrePersist
    protected void onUpload() {
        uploadedAt = LocalDateTime.now();
    }
}
