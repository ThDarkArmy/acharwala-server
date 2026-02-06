package tda.darkarmy.acharwala.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tda.darkarmy.acharwala.model.DidiProfile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DidiProfileResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String phoneNumber;
    private String aadhaarNumber;
    private String aadharImageUrl;
    private String bankAccountNumber;
    private String bankIFSC;
    private String bankName;
    private String accountHolderName;
    private Double latitude;
    private Double longitude;
    private String location;
    private DidiProfile.ApprovalStatus approvalStatus;
    private String rejectionReason;
    private DidiProfile.TrainingStatus trainingStatus;
    private LocalDateTime trainingCompletedAt;
    private BigDecimal totalEarnings;
    private BigDecimal averageRating;
    private Integer totalOrders;
    private Integer totalSales;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime updatedAt;
}
