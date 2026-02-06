package tda.darkarmy.acharwala.service;

import tda.darkarmy.acharwala.dto.CustomRecipeRequest;
import tda.darkarmy.acharwala.dto.CustomRecipeResponse;
import tda.darkarmy.acharwala.dto.RecipePriceCalculationRequest;
import tda.darkarmy.acharwala.dto.RecipePriceCalculationResponse;

import java.util.List;

public interface CustomRecipeService {
    CustomRecipeResponse createRecipe(CustomRecipeRequest request);
    CustomRecipeResponse updateRecipe(Long recipeId, CustomRecipeRequest request);
    CustomRecipeResponse getRecipeById(Long recipeId);
    List<CustomRecipeResponse> getUserRecipes();
    List<CustomRecipeResponse> getUserSavedRecipes();
    CustomRecipeResponse getRecipeByShareToken(String shareToken);
    void deleteRecipe(Long recipeId);
    RecipePriceCalculationResponse calculatePrice(RecipePriceCalculationRequest request);
    CustomRecipeResponse saveRecipe(Long recipeId);
    CustomRecipeResponse shareRecipe(Long recipeId);
}
