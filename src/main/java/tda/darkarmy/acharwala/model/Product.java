package tda.darkarmy.acharwala.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category; // Achar, Papad, Chutney

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String brand;

    private LocalDate expiryDate;

    private LocalDate manufacturingDate;

    private String image;

    private String qrCode;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount; // Weight/volume amount (e.g., 500g, 1kg)

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @Column(nullable = false)
    private Integer numberOfQuantities; // Stock quantity

    @Column(precision = 5, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO; // Discount percentage or amount

    // New attributes for pickle customization platform
    private String oilType;

    @ElementCollection
    @CollectionTable(name = "product_ingredients", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "ingredient")
    private List<String> ingredients = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isCustomizable = false;

    // Relationships
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Version
    private Long version; // For optimistic locking

    // Helper method to calculate discounted price
    public BigDecimal getDiscountedPrice() {
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountAmount = price.multiply(discount.divide(BigDecimal.valueOf(100)));
            return price.subtract(discountAmount);
        }
        return price;
    }

    // Helper method to check if product is in stock
    public Boolean isInStock() {
        return isAvailable && numberOfQuantities > 0;
    }

    // Helper method to decrease inventory
    public void decreaseInventory(Integer quantity) {
        if (this.numberOfQuantities >= quantity) {
            this.numberOfQuantities -= quantity;
            if (this.numberOfQuantities == 0) {
                this.isAvailable = false;
            }
        } else {
            throw new IllegalStateException("Insufficient stock for product: " + this.name);
        }
    }

    // Helper method to increase inventory
    public void increaseInventory(Integer quantity) {
        this.numberOfQuantities += quantity;
        if (this.numberOfQuantities > 0) {
            this.isAvailable = true;
        }
    }

    // PrePersist and PreUpdate methods for validation
    @PrePersist
    @PreUpdate
    public void validateDates() {
        if (manufacturingDate != null && expiryDate != null && expiryDate.isBefore(manufacturingDate)) {
            throw new IllegalStateException("Expiry date cannot be before manufacturing date");
        }

        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            this.isAvailable = false;
        }
    }
}
