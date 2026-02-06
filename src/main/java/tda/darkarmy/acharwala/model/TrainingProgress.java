package tda.darkarmy.acharwala.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "didi_profile_id", nullable = false)
    private DidiProfile didiProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_content_id", nullable = false)
    private TrainingContent trainingContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Integer progressPercentage = 0;

    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ProgressStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED
    }
}
