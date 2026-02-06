package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tda.darkarmy.acharwala.model.CustomRecipe;
import tda.darkarmy.acharwala.model.User;

import java.util.List;
import java.util.Optional;

public interface CustomRecipeRepository extends JpaRepository<CustomRecipe, Long> {
    List<CustomRecipe> findByUser(User user);
    List<CustomRecipe> findByUserAndStatus(User user, CustomRecipe.RecipeStatus status);
    Optional<CustomRecipe> findByShareToken(String shareToken);
    List<CustomRecipe> findByStatus(CustomRecipe.RecipeStatus status);
}
