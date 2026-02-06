package tda.darkarmy.acharwala.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tda.darkarmy.acharwala.dto.CustomRecipeRequest;
import tda.darkarmy.acharwala.dto.CustomRecipeResponse;
import tda.darkarmy.acharwala.dto.RecipePriceCalculationRequest;
import tda.darkarmy.acharwala.dto.RecipePriceCalculationResponse;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Tag(name = "Custom Recipe APIs", description = "APIs for creating and managing custom pickle recipes")
public class CustomRecipeController {

    private final tda.darkarmy.acharwala.service.CustomRecipeService recipeService;

    @Operation(summary = "Create a custom recipe", description = "Create a new custom pickle recipe")
    @PostMapping
    public ResponseEntity<CustomRecipeResponse> createRecipe(@Valid @RequestBody CustomRecipeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipeService.createRecipe(request));
    }

    @Operation(summary = "Update a recipe", description = "Update an existing custom recipe")
    @PutMapping("/{recipeId}")
    public ResponseEntity<CustomRecipeResponse> updateRecipe(
            @PathVariable Long recipeId,
            @Valid @RequestBody CustomRecipeRequest request) {
        return ResponseEntity.ok(recipeService.updateRecipe(recipeId, request));
    }

    @Operation(summary = "Get recipe by ID", description = "Retrieve a specific recipe by its ID")
    @GetMapping("/{recipeId}")
    public ResponseEntity<CustomRecipeResponse> getRecipeById(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeService.getRecipeById(recipeId));
    }

    @Operation(summary = "Get user's recipes", description = "Get all recipes created by the logged-in user")
    @GetMapping("/my-recipes")
    public ResponseEntity<List<CustomRecipeResponse>> getUserRecipes() {
        return ResponseEntity.ok(recipeService.getUserRecipes());
    }

    @Operation(summary = "Get user's saved recipes", description = "Get all saved recipes by the logged-in user")
    @GetMapping("/my-recipes/saved")
    public ResponseEntity<List<CustomRecipeResponse>> getUserSavedRecipes() {
        return ResponseEntity.ok(recipeService.getUserSavedRecipes());
    }

    @Operation(summary = "Get recipe by share token", description = "Retrieve a recipe using its shareable token (public)")
    @GetMapping("/share/{shareToken}")
    public ResponseEntity<CustomRecipeResponse> getRecipeByShareToken(@PathVariable String shareToken) {
        return ResponseEntity.ok(recipeService.getRecipeByShareToken(shareToken));
    }

    @Operation(summary = "Calculate recipe price", description = "Calculate the price for a custom recipe before creating it")
    @PostMapping("/calculate-price")
    public ResponseEntity<RecipePriceCalculationResponse> calculatePrice(
            @Valid @RequestBody RecipePriceCalculationRequest request) {
        return ResponseEntity.ok(recipeService.calculatePrice(request));
    }

    @Operation(summary = "Save recipe", description = "Mark a draft recipe as saved")
    @PostMapping("/{recipeId}/save")
    public ResponseEntity<CustomRecipeResponse> saveRecipe(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeService.saveRecipe(recipeId));
    }

    @Operation(summary = "Share recipe", description = "Mark a recipe as shared and generate shareable link")
    @PostMapping("/{recipeId}/share")
    public ResponseEntity<CustomRecipeResponse> shareRecipe(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeService.shareRecipe(recipeId));
    }

    @Operation(summary = "Delete recipe", description = "Delete a recipe")
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }
}
