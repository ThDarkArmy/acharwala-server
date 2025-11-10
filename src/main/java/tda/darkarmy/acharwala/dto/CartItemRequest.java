package tda.darkarmy.acharwala.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Positive(message = "Quantity must be positive")
    private Integer quantity = 1;

    private String customizationNotes;
}