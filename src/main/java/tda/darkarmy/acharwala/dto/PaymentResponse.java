// package tda.darkarmy.acharwala.dto;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import tda.darkarmy.acharwala.model.PaymentEntity;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class PaymentResponse {
//     private Long id;
//     private String paymentId;
//     private Long orderId;
//     private BigDecimal amount;
//     private String currency;
//     private PaymentEntity.PaymentStatus status;
//     private String paymentMethod;
//     private String razorpayOrderId;
//     private String razorpayPaymentId;
//     private LocalDateTime createdAt;
//     private LocalDateTime completedAt;
//     private String failureReason;
    
//     // For payment initiation
//     private String orderIdForRazorpay; // Razorpay order ID to be used in frontend
//     private String keyId; // Razorpay key ID for frontend
// }
