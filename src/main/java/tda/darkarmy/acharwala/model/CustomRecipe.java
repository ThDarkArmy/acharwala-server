package tda.darkarmy.acharwala.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "custom_recipes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Recipe ingredients
    @ElementCollection
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient")
    private List<String> ingredients = new ArrayList<>();

    // Oil type selection
    private String oilType; // mustard, sesame, groundnut, etc.

    // Spice level (optional)
    private String spiceLevel; // mild, medium, hot, extra-hot

    // Recipe configuration as JSON
    @Column(columnDefinition = "TEXT")
    private String recipeJson; // Store full recipe configuration

    // Calculated price
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    // Final calculated price (base + ingredients + customization)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    // Shareable link token
    @Column(unique = true)
    private String shareToken;

    // Recipe status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipeStatus status = RecipeStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.shareToken == null) {
            this.shareToken = generateShareToken();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private String generateShareToken() {
        return "RECIPE_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 10000);
    }

    public enum RecipeStatus {
        DRAFT, SAVED, ORDERED, SHARED
    }
}
