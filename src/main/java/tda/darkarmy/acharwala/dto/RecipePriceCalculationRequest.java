package tda.darkarmy.acharwala.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipePriceCalculationRequest {
    @NotNull(message = "Base price is required")
    private BigDecimal basePrice;

    @NotNull(message = "Ingredients list is required")
    private List<String> ingredients;

    @NotNull(message = "Oil type is required")
    private String oilType;

    private String spiceLevel;
}
