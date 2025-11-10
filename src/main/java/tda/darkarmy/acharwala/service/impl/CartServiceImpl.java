package tda.darkarmy.acharwala.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tda.darkarmy.acharwala.dto.CartItemRequest;
import tda.darkarmy.acharwala.exception.ResourceNotFoundException;
import tda.darkarmy.acharwala.model.Cart;
import tda.darkarmy.acharwala.model.CartItem;
import tda.darkarmy.acharwala.model.Product;
import tda.darkarmy.acharwala.model.User;
import tda.darkarmy.acharwala.repository.CartItemRepository;
import tda.darkarmy.acharwala.repository.CartRepository;
import tda.darkarmy.acharwala.repository.ProductRepository;
import tda.darkarmy.acharwala.service.CartService;
import tda.darkarmy.acharwala.service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final UserService userService;

    @Override
    public Cart getOrCreateCart() {
        User user = userService.getLoggedInUser();
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if(cartOptional.isPresent()){
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        }else {
            return cartOptional.get();
        }
    }

    @Override
    public Cart addItemToCart(CartItemRequest cartItemRequest) {
        Cart cart = getOrCreateCart();
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getIsAvailable() || product.getNumberOfQuantities() < cartItemRequest.getQuantity()) {
            throw new RuntimeException("Product not available or insufficient stock");
        }

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + cartItemRequest.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(cartItemRequest.getQuantity());
            newItem.setPriceAtAdd(product.getPrice());
            newItem.setCustomizationNotes(cartItemRequest.getCustomizationNotes());
            cartItemRepository.save(newItem);
            cart.getCartItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart updateCartItemQuantity(Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        Cart cart = getOrCreateCart();
        CartItem cartItem = cartItemRepository.findByIdAndCart(cartItemId, cart)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (cartItem.getProduct().getNumberOfQuantities() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItemFromCart(Long cartItemId) {
        Cart cart = getOrCreateCart();
        CartItem cartItem = cartItemRepository.findByIdAndCart(cartItemId, cart)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public Cart clearCart() {
        Cart cart = getOrCreateCart();
        cartItemRepository.deleteAllByCart(cart);
        cart.getCartItems().clear();
        return cartRepository.save(cart);
    }

    @Override
    public List<CartItem> getCartItems() {
        Cart cart = getOrCreateCart();
        return cart.getCartItems();
    }

    @Override
    public BigDecimal getCartTotal() {
        Cart cart = getOrCreateCart();
        return cart.getTotalAmount();
    }

    @Override
    public Integer getCartItemsCount() {
        Cart cart = getOrCreateCart();
        return cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Override
    public Cart mergeCarts(Cart sessionCart) {
        Cart userCart = getOrCreateCart();

        if (sessionCart != null && sessionCart.getCartItems() != null) {
            for (CartItem sessionItem : sessionCart.getCartItems()) {
                Optional<CartItem> existingItem = userCart.getCartItems().stream()
                        .filter(item -> item.getProduct().getId().equals(sessionItem.getProduct().getId()))
                        .findFirst();

                if (existingItem.isPresent()) {
                    CartItem item = existingItem.get();
                    item.setQuantity(item.getQuantity() + sessionItem.getQuantity());
                } else {
                    CartItem newItem = new CartItem();
                    newItem.setCart(userCart);
                    newItem.setProduct(sessionItem.getProduct());
                    newItem.setQuantity(sessionItem.getQuantity());
                    newItem.setPriceAtAdd(sessionItem.getPriceAtAdd());
                    newItem.setCustomizationNotes(sessionItem.getCustomizationNotes());
                    userCart.getCartItems().add(newItem);
                }
            }
        }
        return cartRepository.save(userCart);
    }
}
