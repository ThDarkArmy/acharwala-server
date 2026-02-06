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

    @Override
    public Product createProductFromRequest(tda.darkarmy.acharwala.dto.CreateProductRequest req) {
        String imageUrl = null;
        if (req.getImageFile() != null && !req.getImageFile().isEmpty()) {
            imageUrl = fileStorageService.storeFile(req.getImageFile());
        }
        Product product = Product.builder()
                .name(req.getName())
                .category(req.getCategory())
                .price(req.getPrice())
                .brand(req.getBrand())
                .expiryDate(req.getExpiryDate())
                .manufacturingDate(req.getManufacturingDate())
                .image(imageUrl)
                .numberOfQuantities(req.getStockQuantity())
                .discount(req.getDiscount() != null ? req.getDiscount() : java.math.BigDecimal.ZERO)
                .isAvailable(req.getAvailable() != null ? req.getAvailable() : true)
                .oilType(req.getOilType())
                .ingredients(req.getIngredients() != null ? req.getIngredients() : java.util.List.of())
                .description(req.getDescription())
                .isCustomizable(req.getIsCustomizable() != null ? req.getIsCustomizable() : false)
                .qrCode(req.getQrCode())
                .amount(req.getAmount())
                .build();
        return productRepository.save(product);
    }

    @Override
    public Product updateProductFromRequest(Long id, tda.darkarmy.acharwala.dto.UpdateProductRequest req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new tda.darkarmy.acharwala.exception.ResourceNotFoundException("Product not found"));
        if (req.getName() != null) product.setName(req.getName());
        if (req.getCategory() != null) product.setCategory(req.getCategory());
        if (req.getPrice() != null) product.setPrice(req.getPrice());
        if (req.getBrand() != null) product.setBrand(req.getBrand());
        if (req.getExpiryDate() != null) product.setExpiryDate(req.getExpiryDate());
        if (req.getManufacturingDate() != null) product.setManufacturingDate(req.getManufacturingDate());
        if (req.getStockQuantity() != null) product.setNumberOfQuantities(req.getStockQuantity());
        if (req.getDiscount() != null) product.setDiscount(req.getDiscount());
        if (req.getAvailable() != null) product.setIsAvailable(req.getAvailable());
        if (req.getOilType() != null) product.setOilType(req.getOilType());
        if (req.getIngredients() != null) product.setIngredients(req.getIngredients());
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        if (req.getIsCustomizable() != null) product.setIsCustomizable(req.getIsCustomizable());
        if (req.getQrCode() != null) product.setQrCode(req.getQrCode());
        if (req.getAmount() != null) product.setAmount(req.getAmount());
        if (req.getImageFile() != null && !req.getImageFile().isEmpty()) {
            if (product.getImage() != null) {
                fileStorageService.deleteFile(product.getImage());
            }
            String imageUrl = fileStorageService.storeFile(req.getImageFile());
            product.setImage(imageUrl);
        }
        return productRepository.save(product);
    }
}