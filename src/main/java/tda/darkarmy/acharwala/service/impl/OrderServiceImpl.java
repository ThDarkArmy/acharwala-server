package tda.darkarmy.acharwala.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tda.darkarmy.acharwala.dto.OrderRequest;
import tda.darkarmy.acharwala.enums.Role;
import tda.darkarmy.acharwala.exception.ResourceNotFoundException;
import tda.darkarmy.acharwala.model.*;
import tda.darkarmy.acharwala.repository.*;
import tda.darkarmy.acharwala.service.OrderService;
import tda.darkarmy.acharwala.service.CartService;
import tda.darkarmy.acharwala.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private UserService userService;
    private UserRepository userRepository;

    @Override
    public Order createOrder(OrderRequest orderRequest) {
        User user = userService.getLoggedInUser();
        Cart cart = cartService.getOrCreateCart();

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate stock availability
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            if (product.getNumberOfQuantities() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setBillingAddress(orderRequest.getBillingAddress() != null ?
                orderRequest.getBillingAddress() : orderRequest.getShippingAddress());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Convert cart items to order items and calculate total
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem(cartItem);
            orderItem.setOrder(order);
            orderItemRepository.save(orderItem);
            order.getOrderItems().add(orderItem);

            totalAmount = totalAmount.add(orderItem.getTotalPrice());

            // Update product inventory
            Product product = cartItem.getProduct();
            product.decreaseInventory(cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        order.setFinalAmount(calculateFinalAmount(totalAmount, orderRequest.getShippingCharge()));
        order.setShippingCharge(orderRequest.getShippingCharge());

        Order savedOrder = orderRepository.save(order);

        // Clear the cart after successful order creation
        cartService.clearCart();

        return savedOrder;
    }

    private BigDecimal calculateFinalAmount(BigDecimal totalAmount, BigDecimal shippingCharge) {
        BigDecimal shipping = shippingCharge != null ? shippingCharge : BigDecimal.ZERO;
        return totalAmount.add(shipping);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<Order> getUserOrders() {
        User user = userService.getLoggedInUser();
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    @Override
    public Page<Order> getUserOrdersPaginated(Pageable pageable) {
        User user = userService.getLoggedInUser();
        return orderRepository.findByUser(user, pageable);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Override
    public Page<Order> getAllOrdersPaginated(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long orderId) {
        User user = userService.getLoggedInUser();
        Order order = getOrderById(orderId);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to cancel this order");
        }

        if (order.getStatus().ordinal() >= Order.OrderStatus.SHIPPED.ordinal()) {
            throw new RuntimeException("Cannot cancel order after it has been shipped");
        }

        // Restore product inventory
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getProduct() != null) {
                Product product = orderItem.getProduct();
                product.increaseInventory(orderItem.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setPaymentStatus(Order.PaymentStatus.REFUNDED);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public Order processPaymentSuccess(String paymentId, String transactionId) {
        Order order = orderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for payment ID: " + paymentId));

        order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
        order.setTransactionId(transactionId);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public Order processPaymentFailure(String paymentId, String errorMessage) {
        Order order = orderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for payment ID: " + paymentId));

        order.setPaymentStatus(Order.PaymentStatus.FAILED);
        order.setStatus(Order.OrderStatus.FAILED);
        order.setUpdatedAt(LocalDateTime.now());

        // Restore inventory if payment fails
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getProduct() != null) {
                Product product = orderItem.getProduct();
                product.increaseInventory(orderItem.getQuantity());
                productRepository.save(product);
            }
        }

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatusOrderByOrderDateDesc(status);
    }

    @Override
    public Order assignToSHG(Long orderId, Long shgUserId) {
        User shgUser = userRepository.findById(shgUserId).orElseThrow(()-> new ResourceNotFoundException("SHG didi not found"));
        if (!shgUser.getRole().equals(Role.SHG_DIDI)) {
            throw new RuntimeException("User is not an SHG Didi");
        }

        Order order = getOrderById(orderId);
        order.setAssignedSHG(shgUser);
        order.setStatus(Order.OrderStatus.PROCESSING);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public Order assignToDeliveryBoy(Long orderId, Long deliveryBoyId) {
        User deliveryBoy = userRepository.findById(deliveryBoyId).orElseThrow(()-> new ResourceNotFoundException("Delivery boy not found"));
        if (!deliveryBoy.getRole().equals(Role.DELIVERY_BOY)) {
            throw new RuntimeException("User is not a delivery boy");
        }

        Order order = getOrderById(orderId);
        order.setDeliveryBoy(deliveryBoy);
        order.setStatus(Order.OrderStatus.OUT_FOR_DELIVERY);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByAssignedSHG(User shgUser) {
        return List.of();
    }

    @Override
    public List<Order> getOrdersByDeliveryBoy(User deliveryBoy) {
        return List.of();
    }
}