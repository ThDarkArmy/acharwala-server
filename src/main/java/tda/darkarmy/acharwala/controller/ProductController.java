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
import org.springframework.data.domain.Pageable;
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
            @Valid
            @Parameter(
                    description = "Product JSON data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductDto.class)
                    )
            )
            @ModelAttribute ProductDto productDto){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(productDto));
    }

    // 2. Update Product (with optional image)
    @Operation(
            summary = "Update a product",
            description = "Update product details with optional new image"
    )
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid
            @Parameter(
                    description = "Product JSON data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductDto.class)
                    )
            )
            @RequestPart("productDto") ProductDto productDto,

            @Parameter(
                    description = "Optional product image file",
                    required = false,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            )
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        return ResponseEntity.ok(
                productService.updateProductWithImage(id, productDto, imageFile)
        );
    }

    // 3. Get Product by ID
    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // 4. Get All Products (Paginated)
    @Operation(summary = "Get all products (paginated)")
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
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
