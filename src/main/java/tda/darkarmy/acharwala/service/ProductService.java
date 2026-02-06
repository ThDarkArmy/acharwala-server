package tda.darkarmy.acharwala.service;

import tda.darkarmy.acharwala.dto.ProductDto;
import tda.darkarmy.acharwala.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductService {
    // Takes DTO, returns Entity
    Product createProduct(ProductDto productDto);
    Product createProductFromRequest(tda.darkarmy.acharwala.dto.CreateProductRequest createProductRequest);
    Product updateProductWithImage(Long id, ProductDto productDto, MultipartFile imageFile);
    Product updateProductFromRequest(Long id, tda.darkarmy.acharwala.dto.UpdateProductRequest updateProductRequest);

    // Read operations return Entities
    Product getProductById(Long id);
    List<Product> getAllProducts();
    Page<Product> getAllProducts(Pageable pageable);
    List<Product> getProductsByCategory(String category);
    List<Product> getAvailableProducts();
    List<Product> getDiscountedProducts(double minDiscount);

    // Inventory management
    Product updateStock(Long productId, int quantityChange);
    Product toggleAvailability(Long productId);

    // Bulk operations
    List<Product> createProductsInBatch(List<ProductDto> productDtos);
}