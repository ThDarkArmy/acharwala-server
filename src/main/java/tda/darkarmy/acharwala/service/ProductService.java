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
    Product updateProductWithImage(Long id, ProductDto productDto, MultipartFile imageFile);

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