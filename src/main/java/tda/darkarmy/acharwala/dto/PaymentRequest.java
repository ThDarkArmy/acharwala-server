package tda.darkarmy.acharwala.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String currency = "INR";
    private String paymentMethod; // CARD, UPI, NETBANKING, etc.
}
