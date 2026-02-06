package tda.darkarmy.acharwala.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateProductRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String category;
    @NotNull
    private BigDecimal price;
    @NotBlank
    private String brand;
    private LocalDate expiryDate;
    private LocalDate manufacturingDate;
    private MultipartFile imageFile;  // image upload only
    @PositiveOrZero
    private Integer stockQuantity;
    private BigDecimal discount;      // percentage or amount
    private Boolean available;
    private String oilType;
    private List<String> ingredients;
    private String description;
    private Boolean isCustomizable;
    private String qrCode;
    private BigDecimal amount;
}