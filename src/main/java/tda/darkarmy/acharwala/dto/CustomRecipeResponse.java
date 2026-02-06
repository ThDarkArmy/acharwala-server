package tda.darkarmy.acharwala.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tda.darkarmy.acharwala.model.CustomRecipe;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomRecipeResponse {
    private Long id;
    private String name;
    private Long userId;
    private String userName;
    private String description;
    private List<String> ingredients;
    private String oilType;
    private String spiceLevel;
    private String recipeJson;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private String shareToken;
    private CustomRecipe.RecipeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String shareableLink; // Full URL for sharing
}
