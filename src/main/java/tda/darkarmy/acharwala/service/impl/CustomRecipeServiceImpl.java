package tda.darkarmy.acharwala.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tda.darkarmy.acharwala.dto.CustomRecipeRequest;
import tda.darkarmy.acharwala.dto.CustomRecipeResponse;
import tda.darkarmy.acharwala.dto.RecipePriceCalculationRequest;
import tda.darkarmy.acharwala.dto.RecipePriceCalculationResponse;
import tda.darkarmy.acharwala.exception.ResourceNotFoundException;
import tda.darkarmy.acharwala.model.CustomRecipe;
import tda.darkarmy.acharwala.model.User;
import tda.darkarmy.acharwala.repository.CustomRecipeRepository;
import tda.darkarmy.acharwala.service.CustomRecipeService;
import tda.darkarmy.acharwala.service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomRecipeServiceImpl implements CustomRecipeService {

    private final CustomRecipeRepository recipeRepository;
    private final UserService userService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // Pricing configuration (can be moved to database/config)
    private static final BigDecimal INGREDIENT_BASE_COST = BigDecimal.valueOf(10); // Per ingredient
    private static final BigDecimal OIL_TYPE_PREMIUM = BigDecimal.valueOf(20); // Premium oil types
    private static final BigDecimal SPICE_LEVEL_COST = BigDecimal.valueOf(5); // Per spice level increment

    @Override
    public CustomRecipeResponse createRecipe(CustomRecipeRequest request) {
        User user = userService.getLoggedInUser();

        // Calculate total price
        BigDecimal totalPrice = calculateTotalPrice(
                request.getBasePrice(),
                request.getIngredients(),
                request.getOilType(),
                request.getSpiceLevel()
        );

        // Override with custom price if provided
        if (request.getCustomPrice() != null) {
            totalPrice = request.getCustomPrice();
        }

        CustomRecipe recipe = CustomRecipe.builder()
                .name(request.getName())
                .user(user)
                .description(request.getDescription())
                .ingredients(request.getIngredients())
                .oilType(request.getOilType())
                .spiceLevel(request.getSpiceLevel())
                .recipeJson(request.getRecipeJson())
                .basePrice(request.getBasePrice())
                .totalPrice(totalPrice)
                .status(CustomRecipe.RecipeStatus.DRAFT)
                .build();

        recipe = recipeRepository.save(recipe);

        return mapToResponse(recipe);
    }

    @Override
    public CustomRecipeResponse updateRecipe(Long recipeId, CustomRecipeRequest request) {
        CustomRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        User user = userService.getLoggedInUser();
        if (!recipe.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this recipe");
        }

        // Recalculate price if ingredients/oil/spice changed
        BigDecimal totalPrice = calculateTotalPrice(
                request.getBasePrice() != null ? request.getBasePrice() : recipe.getBasePrice(),
                request.getIngredients() != null ? request.getIngredients() : recipe.getIngredients(),
                request.getOilType() != null ? request.getOilType() : recipe.getOilType(),
                request.getSpiceLevel() != null ? request.getSpiceLevel() : recipe.getSpiceLevel()
        );

        if (request.getCustomPrice() != null) {
            totalPrice = request.getCustomPrice();
        }

        // Update fields
        if (request.getName() != null) recipe.setName(request.getName());
        if (request.getDescription() != null) recipe.setDescription(request.getDescription());
        if (request.getIngredients() != null) recipe.setIngredients(request.getIngredients());
        if (request.getOilType() != null) recipe.setOilType(request.getOilType());
        if (request.getSpiceLevel() != null) recipe.setSpiceLevel(request.getSpiceLevel());
        if (request.getRecipeJson() != null) recipe.setRecipeJson(request.getRecipeJson());
        if (request.getBasePrice() != null) recipe.setBasePrice(request.getBasePrice());
        recipe.setTotalPrice(totalPrice);

        recipe = recipeRepository.save(recipe);

        return mapToResponse(recipe);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomRecipeResponse getRecipeById(Long recipeId) {
        CustomRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        return mapToResponse(recipe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomRecipeResponse> getUserRecipes() {
        User user = userService.getLoggedInUser();
        List<CustomRecipe> recipes = recipeRepository.findByUser(user);
        return recipes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomRecipeResponse> getUserSavedRecipes() {
        User user = userService.getLoggedInUser();
        List<CustomRecipe> recipes = recipeRepository.findByUserAndStatus(
                user, CustomRecipe.RecipeStatus.SAVED);
        return recipes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomRecipeResponse getRecipeByShareToken(String shareToken) {
        CustomRecipe recipe = recipeRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        return mapToResponse(recipe);
    }

    @Override
    public void deleteRecipe(Long recipeId) {
        CustomRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        User user = userService.getLoggedInUser();
        if (!recipe.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this recipe");
        }

        recipeRepository.delete(recipe);
    }

    @Override
    public RecipePriceCalculationResponse calculatePrice(RecipePriceCalculationRequest request) {
        BigDecimal basePrice = request.getBasePrice();
        BigDecimal ingredientsCost = calculateIngredientsCost(request.getIngredients());
        BigDecimal oilTypeCost = calculateOilTypeCost(request.getOilType());
        BigDecimal spiceLevelCost = calculateSpiceLevelCost(request.getSpiceLevel());

        BigDecimal totalPrice = basePrice
                .add(ingredientsCost)
                .add(oilTypeCost)
                .add(spiceLevelCost);

        String breakdown = String.format(
                "Base Price: ₹%.2f + Ingredients (%d): ₹%.2f + Oil Type (%s): ₹%.2f + Spice Level (%s): ₹%.2f = Total: ₹%.2f",
                basePrice,
                request.getIngredients().size(),
                ingredientsCost,
                request.getOilType(),
                oilTypeCost,
                request.getSpiceLevel() != null ? request.getSpiceLevel() : "medium",
                spiceLevelCost,
                totalPrice
        );

        return RecipePriceCalculationResponse.builder()
                .basePrice(basePrice)
                .ingredientsCost(ingredientsCost)
                .oilTypeCost(oilTypeCost)
                .spiceLevelCost(spiceLevelCost)
                .totalPrice(totalPrice)
                .breakdown(breakdown)
                .build();
    }

    @Override
    public CustomRecipeResponse saveRecipe(Long recipeId) {
        CustomRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        User user = userService.getLoggedInUser();
        if (!recipe.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to save this recipe");
        }

        recipe.setStatus(CustomRecipe.RecipeStatus.SAVED);
        recipe = recipeRepository.save(recipe);

        return mapToResponse(recipe);
    }

    @Override
    public CustomRecipeResponse shareRecipe(Long recipeId) {
        CustomRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        User user = userService.getLoggedInUser();
        if (!recipe.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to share this recipe");
        }

        recipe.setStatus(CustomRecipe.RecipeStatus.SHARED);
        recipe = recipeRepository.save(recipe);

        return mapToResponse(recipe);
    }

    private BigDecimal calculateTotalPrice(BigDecimal basePrice, List<String> ingredients,
                                           String oilType, String spiceLevel) {
        BigDecimal ingredientsCost = calculateIngredientsCost(ingredients);
        BigDecimal oilTypeCost = calculateOilTypeCost(oilType);
        BigDecimal spiceLevelCost = calculateSpiceLevelCost(spiceLevel);

        return basePrice
                .add(ingredientsCost)
                .add(oilTypeCost)
                .add(spiceLevelCost);
    }

    private BigDecimal calculateIngredientsCost(List<String> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return INGREDIENT_BASE_COST.multiply(BigDecimal.valueOf(ingredients.size()));
    }

    private BigDecimal calculateOilTypeCost(String oilType) {
        if (oilType == null) {
            return BigDecimal.ZERO;
        }
        // Premium oils cost more
        if (oilType.equalsIgnoreCase("sesame") || oilType.equalsIgnoreCase("olive")) {
            return OIL_TYPE_PREMIUM;
        }
        return BigDecimal.ZERO; // Default oils (mustard, groundnut) have no extra cost
    }

    private BigDecimal calculateSpiceLevelCost(String spiceLevel) {
        if (spiceLevel == null || spiceLevel.equalsIgnoreCase("mild")) {
            return BigDecimal.ZERO;
        }
        switch (spiceLevel.toLowerCase()) {
            case "medium":
                return SPICE_LEVEL_COST;
            case "hot":
                return SPICE_LEVEL_COST.multiply(BigDecimal.valueOf(2));
            case "extra-hot":
                return SPICE_LEVEL_COST.multiply(BigDecimal.valueOf(3));
            default:
                return BigDecimal.ZERO;
        }
    }

    private CustomRecipeResponse mapToResponse(CustomRecipe recipe) {
        String shareableLink = baseUrl + "/api/recipes/share/" + recipe.getShareToken();

        return CustomRecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .userId(recipe.getUser().getId())
                .userName(recipe.getUser().getName())
                .description(recipe.getDescription())
                .ingredients(recipe.getIngredients())
                .oilType(recipe.getOilType())
                .spiceLevel(recipe.getSpiceLevel())
                .recipeJson(recipe.getRecipeJson())
                .basePrice(recipe.getBasePrice())
                .totalPrice(recipe.getTotalPrice())
                .shareToken(recipe.getShareToken())
                .status(recipe.getStatus())
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .shareableLink(shareableLink)
                .build();
    }
}
