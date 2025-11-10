package tda.darkarmy.acharwala.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity = 1;

    // For custom pickles
    private String customizationNotes;

    // Snapshot of price when added to cart (in case price changes later)
    @Column(precision = 10, scale = 2)
    private BigDecimal priceAtAdd;

    @PrePersist
    @PreUpdate
    public void calculatePrice() {
        if (product != null && priceAtAdd == null) {
            this.priceAtAdd = product.getPrice();
        }
    }

    public BigDecimal getSubTotal() {
        return priceAtAdd.multiply(BigDecimal.valueOf(quantity));
    }
}
