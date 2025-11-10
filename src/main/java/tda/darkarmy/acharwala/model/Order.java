package tda.darkarmy.acharwala.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, OUT_FOR_DELIVERY,
        DELIVERED, CANCELLED, REFUNDED, FAILED
    }

    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED, REFUNDED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    // NEW: Added updatedAt field
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal shippingCharge = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "shipping_street")),
            @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
            @AttributeOverride(name = "state", column = @Column(name = "shipping_state")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "shipping_country")),
            @AttributeOverride(name = "landmark", column = @Column(name = "shipping_landmark")),
            @AttributeOverride(name = "contactNumber", column = @Column(name = "shipping_contact")),
            @AttributeOverride(name = "recipientName", column = @Column(name = "shipping_recipient"))
    })
    private Address shippingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "billing_street")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "billing_country")),
            @AttributeOverride(name = "landmark", column = @Column(name = "billing_landmark")),
            @AttributeOverride(name = "contactNumber", column = @Column(name = "billing_contact")),
            @AttributeOverride(name = "recipientName", column = @Column(name = "billing_recipient"))
    })
    private Address billingAddress;

    private String paymentMethod;
    private String paymentId;
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_shg_id")
    private User assignedSHG;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_boy_id")
    private User deliveryBoy;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Version
    private Long version;

    @PrePersist
    public void onPrePersist() {
        if (this.orderNumber == null) {
            this.orderNumber = "ORD-" + LocalDateTime.now().getYear() + "-" +
                    String.format("%03d", (int)(Math.random() * 1000));
        }
        this.orderDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}