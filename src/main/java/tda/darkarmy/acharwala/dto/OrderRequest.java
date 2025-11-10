package tda.darkarmy.acharwala.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tda.darkarmy.acharwala.model.Address;

import java.math.BigDecimal;

@Data
public class OrderRequest {
    @NotNull(message = "Shipping address is required")
    @Valid
    private Address shippingAddress;

    @Valid
    private Address billingAddress;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    private BigDecimal shippingCharge = BigDecimal.ZERO;

    private String notes;
}
