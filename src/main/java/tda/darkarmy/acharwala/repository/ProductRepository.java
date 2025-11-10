package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tda.darkarmy.acharwala.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsAvailableTrue();

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByDiscountGreaterThanEqual(double minDiscount);
}
