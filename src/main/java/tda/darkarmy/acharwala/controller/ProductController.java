package tda.darkarmy.acharwala.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tda.darkarmy.acharwala.dto.ProductDto;
import tda.darkarmy.acharwala.model.Product;
import tda.darkarmy.acharwala.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    // 1. Create Product (with image)
    @Operation(
            summary = "Create a product",
            description = "Create a new product with an image",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Product created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(
            @Valid @ModelAttribute tda.darkarmy.acharwala.dto.CreateProductRequest createProductRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProductFromRequest(createProductRequest));
    }

    // 2. Update Product (with optional image)
    @Operation(
            summary = "Update a product",
            description = "Update product details with optional new image"
    )
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute tda.darkarmy.acharwala.dto.UpdateProductRequest updateProductRequest) {
        return ResponseEntity.ok(productService.updateProductFromRequest(id, updateProductRequest));
    }

    // 3. Get Product by ID
    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "id", "name", "category", "price", "brand", "expiryDate", "manufacturingDate", "numberOfQuantities", "discount", "isAvailable");

    // 4. Get All Products (Paginated)
    @Operation(summary = "Get all products (paginated)")
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by: id, name, category, price, brand, expiryDate, manufacturingDate") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "asc") String direction) {
        // Clamp size to avoid huge requests
        size = Math.min(Math.max(1, size), 100);
        // Use id if sortBy is not a valid Product property (e.g. Swagger placeholder "string")
        String validSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "id";
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(validSortBy).descending() : Sort.by(validSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    // 5. Get Products by Category
    @Operation(summary = "Get products by category")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    // 6. Get Available Products
    @Operation(summary = "Get available products")
    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        return ResponseEntity.ok(productService.getAvailableProducts());
    }

    // 7. Get Discounted Products
    @Operation(summary = "Get discounted products")
    @GetMapping("/discounted")
    public ResponseEntity<List<Product>> getDiscountedProducts(
            @RequestParam double minDiscount) {
        return ResponseEntity.ok(productService.getDiscountedProducts(minDiscount));
    }

    // 8. Update Stock
    @Operation(summary = "Update stock of a product")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }

    // 9. Toggle Availability
    @Operation(summary = "Toggle product availability")
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Product> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(productService.toggleAvailability(id));
    }

    // 10. Bulk Create Products
    @Operation(summary = "Bulk create products")
    @PostMapping("/batch")
    public ResponseEntity<List<Product>> createProductsInBatch(
            @Valid @RequestBody List<ProductDto> productDtos) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProductsInBatch(productDtos));
    }
}
