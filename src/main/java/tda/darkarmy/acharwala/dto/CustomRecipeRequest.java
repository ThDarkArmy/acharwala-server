package tda.darkarmy.acharwala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CustomRecipeRequest {
    @NotBlank(message = "Recipe name is required")
    private String name;

    private String description;

    @NotNull(message = "Ingredients are required")
    private List<String> ingredients;

    @NotBlank(message = "Oil type is required")
    private String oilType; // mustard, sesame, groundnut, etc.

    private String spiceLevel; // mild, medium, hot, extra-hot

    // Recipe configuration as JSON string
    private String recipeJson;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    // Optional: Custom price override
    private BigDecimal customPrice;
}
