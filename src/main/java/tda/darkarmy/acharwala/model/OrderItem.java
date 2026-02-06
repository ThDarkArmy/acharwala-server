package tda.darkarmy.acharwala.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // Can be null if product is deleted later

    @Column(nullable = false)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String productDescription;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    private String oilType;

    @ElementCollection
    @CollectionTable(name = "order_item_ingredients",
            joinColumns = @JoinColumn(name = "order_item_id"))
    @Column(name = "ingredient")
    private List<String> ingredients = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    private String customizationNotes;

    private String imageUrl;

    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity))
                    .subtract(discountAmount);
        }
    }

    // Constructor to create from CartItem
    public OrderItem(CartItem cartItem) {
        this.product = cartItem.getProduct();
        this.productName = cartItem.getProduct().getName();
        this.productDescription = cartItem.getProduct().getDescription();
        this.unitPrice = cartItem.getPriceAtAdd();
        this.quantity = cartItem.getQuantity();
        this.oilType = cartItem.getProduct().getOilType();
        this.ingredients = new ArrayList<>(cartItem.getProduct().getIngredients());
        this.customizationNotes = cartItem.getCustomizationNotes();
        this.imageUrl = cartItem.getProduct().getImage();
        calculateTotalPrice();
    }
}
