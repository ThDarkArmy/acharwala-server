package tda.darkarmy.acharwala.service.impl;

import tda.darkarmy.acharwala.dto.ProductDto;
import tda.darkarmy.acharwala.exception.ResourceNotFoundException;
import tda.darkarmy.acharwala.mapper.ProductMapper;
import tda.darkarmy.acharwala.model.Product;
import tda.darkarmy.acharwala.repository.ProductRepository;
import tda.darkarmy.acharwala.service.ProductService;
import tda.darkarmy.acharwala.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Product createProduct(ProductDto productDto) {
        // Store image and get URL
        String imageUrl = fileStorageService.storeFile(productDto.getImageFile());
        productDto.setImageUrl(imageUrl);

        // Convert DTO to Entity and save
        Product product = ProductMapper.toProductEntity(productDto);
        return productRepository.save(product);
    }

    @Override
    public Product updateProductWithImage(Long id, ProductDto productDto, MultipartFile imageFile) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Handle image update
        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old image if exists
            if (existingProduct.getImage() != null) {
                fileStorageService.deleteFile(existingProduct.getImage());
            }
            String newImageUrl = fileStorageService.storeFile(imageFile);
            productDto.setImageUrl(newImageUrl);
        }

        // Update fields from DTO
        ProductMapper.updateProductFromDto(productDto, existingProduct);
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getDiscountedProducts(double minDiscount) {
        return productRepository.findByDiscountGreaterThanEqual(minDiscount);
    }

    @Override
    public Product updateStock(Long productId, int quantityChange) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        int newQuantity = product.getNumberOfQuantities() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        product.setNumberOfQuantities(newQuantity);
//        product.setAvailable(newQuantity > 0);

        return productRepository.save(product);
    }

    @Override
    public Product toggleAvailability(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

//        product.setAvailable(!product.isAvailable());
        return productRepository.save(product);
    }

    @Override
    public List<Product> createProductsInBatch(List<ProductDto> productDtos) {
        List<Product> products = productDtos.stream()
                .map(ProductMapper::toProductEntity)
                .collect(Collectors.toList());
        return productRepository.saveAll(products);
    }
}