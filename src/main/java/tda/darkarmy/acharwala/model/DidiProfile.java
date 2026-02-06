package tda.darkarmy.acharwala.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "didi_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DidiProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Aadhaar Information
    @Column(unique = true, nullable = false)
    private String aadhaarNumber;

    private String aadharImageUrl;

    // Bank Details
    private String bankAccountNumber;
    private String bankIFSC;
    private String bankName;
    private String accountHolderName;

    // Location Information
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String location; // Address/area name

    // Approval Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    private String rejectionReason;

    // Training Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingStatus trainingStatus = TrainingStatus.NOT_STARTED;

    private LocalDateTime trainingCompletedAt;

    // Performance Metrics
    @Column(precision = 10, scale = 2)
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    private Integer totalOrders = 0;

    private Integer totalSales = 0;

    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime approvedAt;

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED, SUSPENDED
    }

    public enum TrainingStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED
    }
}
