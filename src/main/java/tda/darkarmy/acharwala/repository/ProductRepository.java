package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tda.darkarmy.acharwala.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
