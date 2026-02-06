package tda.darkarmy.acharwala.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class UpdateProductRequest {
    private String name;
    private String category;
    private BigDecimal price;
    private String brand;
    private LocalDate expiryDate;
    private LocalDate manufacturingDate;
    private MultipartFile imageFile;
    private Integer stockQuantity;
    private BigDecimal discount;
    private Boolean available;
    private String oilType;
    private List<String> ingredients;
    private String description;
    private Boolean isCustomizable;
    private String qrCode;
    private BigDecimal amount;
}
