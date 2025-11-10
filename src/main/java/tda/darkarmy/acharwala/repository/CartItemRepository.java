package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tda.darkarmy.acharwala.model.Cart;
import tda.darkarmy.acharwala.model.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndCart(Long cartItemId, Cart cart);

    void deleteAllByCart(Cart cart);
}
