package tda.darkarmy.acharwala.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import tda.darkarmy.acharwala.model.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category; // Achar, Papad, Chutney

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 integer and 2 fraction digits")
    private BigDecimal price;

    @NotBlank(message = "Brand is required")
    private String brand;

    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @PastOrPresent(message = "Manufacturing date must be past or present")
    private LocalDate manufacturingDate;

    private MultipartFile imageFile; // For image uploads
    private String imageUrl; // For storing the final URL

    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer stockQuantity;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Discount must have up to 3 integer and 2 fraction digits")
    private BigDecimal discount;

    private Boolean available;

    // New fields for pickle platform
    private String oilType; // mustard, sesame, etc.

    private List<String> ingredients;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private Boolean isCustomizable = false;

    private String qrCode;

    @Positive(message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 integer and 2 fraction digits")
    private BigDecimal amount; // Weight/volume amount (e.g., 500g, 1kg)

    // Helper method to convert DTO to Entity
    public Product toEntity() {
        return Product.builder()
                .id(this.id)
                .name(this.name)
                .category(this.category)
                .price(this.price)
                .brand(this.brand)
                .expiryDate(this.expiryDate)
                .manufacturingDate(this.manufacturingDate)
                .image(this.imageUrl) // Assuming imageUrl will be set after file upload
                .numberOfQuantities(this.stockQuantity)
                .discount(this.discount)
                .isAvailable(this.available)
                .oilType(this.oilType)
                .ingredients(this.ingredients != null ? this.ingredients : List.of())
                .description(this.description)
                .isCustomizable(this.isCustomizable != null ? this.isCustomizable : false)
                .qrCode(this.qrCode)
                .amount(this.amount)
                .build();
    }

    // Helper method to create DTO from Entity
    public static ProductDto fromEntity(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .brand(product.getBrand())
                .expiryDate(product.getExpiryDate())
                .manufacturingDate(product.getManufacturingDate())
                .imageUrl(product.getImage())
                .stockQuantity(product.getNumberOfQuantities())
                .discount(product.getDiscount())
                .available(product.getIsAvailable())
                .oilType(product.getOilType())
                .ingredients(product.getIngredients())
                .description(product.getDescription())
                .isCustomizable(product.getIsCustomizable())
                .qrCode(product.getQrCode())
                .amount(product.getAmount())
                .build();
    }

    // Helper method to check if product has discount
    public boolean hasDiscount() {
        return discount != null && discount.compareTo(BigDecimal.ZERO) > 0;
    }

    // Helper method to calculate discounted price
    public BigDecimal getDiscountedPrice() {
        if (hasDiscount() && price != null) {
            BigDecimal discountAmount = price.multiply(discount.divide(BigDecimal.valueOf(100)));
            return price.subtract(discountAmount);
        }
        return price;
    }

    // Validation method for manufacturing and expiry dates
    public boolean isValidDateRange() {
        if (manufacturingDate != null && expiryDate != null) {
            return !expiryDate.isBefore(manufacturingDate);
        }
        return true;
    }
}