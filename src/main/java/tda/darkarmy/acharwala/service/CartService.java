package tda.darkarmy.acharwala.service;

import tda.darkarmy.acharwala.dto.CartItemRequest;
import tda.darkarmy.acharwala.model.Cart;
import tda.darkarmy.acharwala.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    Cart getOrCreateCart();
    Cart addItemToCart(CartItemRequest cartItemRequest);
    Cart updateCartItemQuantity(Long cartItemId, Integer quantity);
    Cart removeItemFromCart(Long cartItemId);
    Cart clearCart();
    List<CartItem> getCartItems();
    BigDecimal getCartTotal();
    Integer getCartItemsCount();
    Cart mergeCarts(Cart sessionCart);
}
