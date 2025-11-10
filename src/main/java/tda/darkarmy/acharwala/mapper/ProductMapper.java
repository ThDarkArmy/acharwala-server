package tda.darkarmy.acharwala.mapper;

import tda.darkarmy.acharwala.dto.ProductDto;
import tda.darkarmy.acharwala.model.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductMapper {

    public static ProductDto toProductDto(Product product) {
        if (product == null) {
            return null;
        }

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
                // New fields for pickle platform
                .oilType(product.getOilType())
                .ingredients(product.getIngredients() != null ?
                        List.copyOf(product.getIngredients()) : List.of())
                .description(product.getDescription())
                .isCustomizable(product.getIsCustomizable())
                .qrCode(product.getQrCode())
                .amount(product.getAmount())
                .build();
    }

    public static Product toProductEntity(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }

        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .category(productDto.getCategory())
                .price(productDto.getPrice())
                .brand(productDto.getBrand())
                .expiryDate(productDto.getExpiryDate())
                .manufacturingDate(productDto.getManufacturingDate())
                .image(productDto.getImageUrl())
                .numberOfQuantities(productDto.getStockQuantity())
                .discount(productDto.getDiscount() != null ?
                        productDto.getDiscount() : BigDecimal.ZERO)
                .isAvailable(productDto.getAvailable() != null ?
                        productDto.getAvailable() : true)
                // New fields for pickle platform
                .oilType(productDto.getOilType())
                .ingredients(productDto.getIngredients() != null ?
                        productDto.getIngredients() : List.of())
                .description(productDto.getDescription())
                .isCustomizable(productDto.getIsCustomizable() != null ?
                        productDto.getIsCustomizable() : false)
                .qrCode(productDto.getQrCode())
                .amount(productDto.getAmount())
                .build();
    }

    public static void updateProductFromDto(ProductDto dto, Product entity) {
        if (dto == null || entity == null) return;

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getCategory() != null) entity.setCategory(dto.getCategory());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        if (dto.getBrand() != null) entity.setBrand(dto.getBrand());
        if (dto.getExpiryDate() != null) entity.setExpiryDate(dto.getExpiryDate());
        if (dto.getManufacturingDate() != null) entity.setManufacturingDate(dto.getManufacturingDate());
        if (dto.getImageUrl() != null) entity.setImage(dto.getImageUrl());
        if (dto.getStockQuantity() != null) entity.setNumberOfQuantities(dto.getStockQuantity());
        if (dto.getDiscount() != null) entity.setDiscount(dto.getDiscount());
        if (dto.getAvailable() != null) entity.setIsAvailable(dto.getAvailable());

        // Update new fields for pickle platform
        if (dto.getOilType() != null) entity.setOilType(dto.getOilType());
        if (dto.getIngredients() != null) entity.setIngredients(dto.getIngredients());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getIsCustomizable() != null) entity.setIsCustomizable(dto.getIsCustomizable());
        if (dto.getQrCode() != null) entity.setQrCode(dto.getQrCode());
        if (dto.getAmount() != null) entity.setAmount(dto.getAmount());
    }

    // Additional helper method for partial updates (PATCH requests)
    public static Product partialUpdate(ProductDto dto, Product entity) {
        if (dto == null || entity == null) return entity;

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getCategory() != null) entity.setCategory(dto.getCategory());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        if (dto.getBrand() != null) entity.setBrand(dto.getBrand());
        if (dto.getExpiryDate() != null) entity.setExpiryDate(dto.getExpiryDate());
        if (dto.getManufacturingDate() != null) entity.setManufacturingDate(dto.getManufacturingDate());
        if (dto.getImageUrl() != null) entity.setImage(dto.getImageUrl());
        if (dto.getStockQuantity() != null) entity.setNumberOfQuantities(dto.getStockQuantity());
        if (dto.getDiscount() != null) entity.setDiscount(dto.getDiscount());
        if (dto.getAvailable() != null) entity.setIsAvailable(dto.getAvailable());

        // Update new fields only if provided
        if (dto.getOilType() != null) entity.setOilType(dto.getOilType());
        if (dto.getIngredients() != null && !dto.getIngredients().isEmpty()) {
            entity.setIngredients(dto.getIngredients());
        }
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getIsCustomizable() != null) entity.setIsCustomizable(dto.getIsCustomizable());
        if (dto.getQrCode() != null) entity.setQrCode(dto.getQrCode());
        if (dto.getAmount() != null) entity.setAmount(dto.getAmount());

        return entity;
    }
}