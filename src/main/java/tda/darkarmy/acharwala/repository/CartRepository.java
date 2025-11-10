package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tda.darkarmy.acharwala.model.Cart;
import tda.darkarmy.acharwala.model.User;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
