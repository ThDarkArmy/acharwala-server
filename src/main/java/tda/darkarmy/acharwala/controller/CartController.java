package tda.darkarmy.acharwala.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tda.darkarmy.acharwala.dto.CartItemRequest;
import tda.darkarmy.acharwala.model.Cart;
import tda.darkarmy.acharwala.model.CartItem;
import tda.darkarmy.acharwala.service.CartService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "APIs for managing shopping cart operations")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get user's cart", description = "Retrieve the current user's shopping cart or create a new one if it doesn't exist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Cart> getCart() {
        Cart cart = cartService.getOrCreateCart();
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Add item to cart", description = "Add a product item to the user's shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added to cart successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or product not available"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/items")
    public ResponseEntity<Cart> addItemToCart(@Valid @RequestBody CartItemRequest cartItemRequest) {
        Cart cart = cartService.addItemToCart(cartItemRequest);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Update item quantity", description = "Update the quantity of a specific item in the cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or item not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @Parameter(description = "ID of the cart item to update") @PathVariable Long cartItemId,
            @Parameter(description = "New quantity for the item", example = "2") @RequestParam Integer quantity) {

        Cart cart = cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Remove item from cart", description = "Remove a specific item from the shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found in cart"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> removeItemFromCart(
            @Parameter(description = "ID of the cart item to remove") @PathVariable Long cartItemId) {

        Cart cart = cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Clear cart", description = "Remove all items from the shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get cart items", description = "Retrieve all items in the shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart items retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems() {
        List<CartItem> cartItems = cartService.getCartItems();
        return ResponseEntity.ok(cartItems);
    }

    @Operation(summary = "Get cart total", description = "Calculate the total amount of all items in the cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total calculated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getCartTotal() {
        BigDecimal total = cartService.getCartTotal();
        return ResponseEntity.ok(total);
    }

    @Operation(summary = "Get cart items count", description = "Get the total number of items in the cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemsCount() {
        Integer count = cartService.getCartItemsCount();
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Merge carts", description = "Merge a session cart with the user's persistent cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carts merged successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid cart data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/merge")
    public ResponseEntity<Cart> mergeCarts(@RequestBody Cart sessionCart) {
        Cart cart = cartService.mergeCarts(sessionCart);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Get cart summary", description = "Get a comprehensive summary of the cart including items, total amount, and count")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart summary retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/summary")
    public ResponseEntity<CartSummary> getCartSummary() {
        Cart cart = cartService.getOrCreateCart();
        BigDecimal total = cartService.getCartTotal();
        Integer count = cartService.getCartItemsCount();

        CartSummary summary = new CartSummary(cart, total, count);
        return ResponseEntity.ok(summary);
    }

    // Record for cart summary response
    public record CartSummary(Cart cart, BigDecimal totalAmount, Integer itemsCount) {}
}