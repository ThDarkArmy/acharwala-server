package tda.darkarmy.acharwala.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipePriceCalculationResponse {
    private BigDecimal basePrice;
    private BigDecimal ingredientsCost;
    private BigDecimal oilTypeCost;
    private BigDecimal spiceLevelCost;
    private BigDecimal totalPrice;
    private String breakdown; // Human-readable breakdown
}
