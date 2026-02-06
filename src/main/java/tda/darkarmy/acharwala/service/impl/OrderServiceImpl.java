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
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public OrderEntity createOrder(OrderRequest orderRequest) {
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

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setBillingAddress(orderRequest.getBillingAddress() != null ?
                orderRequest.getBillingAddress() : orderRequest.getShippingAddress());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setStatus(OrderEntity.OrderStatus.PENDING);
        order.setPaymentStatus(OrderEntity.PaymentStatus.PENDING);

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

        OrderEntity savedOrder = orderRepository.save(order);

        // Clear the cart after successful order creation
        cartService.clearCart();

        return savedOrder;
    }

    private BigDecimal calculateFinalAmount(BigDecimal totalAmount, BigDecimal shippingCharge) {
        BigDecimal shipping = shippingCharge != null ? shippingCharge : BigDecimal.ZERO;
        return totalAmount.add(shipping);
    }

    @Override
    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public OrderEntity getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<OrderEntity> getUserOrders() {
        User user = userService.getLoggedInUser();
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    @Override
    public Page<OrderEntity> getUserOrdersPaginated(Pageable pageable) {
        User user = userService.getLoggedInUser();
        return orderRepository.findByUser(user, pageable);
    }

    @Override
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Override
    public Page<OrderEntity> getAllOrdersPaginated(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public OrderEntity updateOrderStatus(Long orderId, OrderEntity.OrderStatus status) {
        OrderEntity order = getOrderById(orderId);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    public OrderEntity cancelOrder(Long orderId) {
        User user = userService.getLoggedInUser();
        OrderEntity order = getOrderById(orderId);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to cancel this order");
        }

        if (order.getStatus().ordinal() >= OrderEntity.OrderStatus.SHIPPED.ordinal()) {
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

        order.setStatus(OrderEntity.OrderStatus.CANCELLED);
        order.setPaymentStatus(OrderEntity.PaymentStatus.REFUNDED);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public OrderEntity processPaymentSuccess(String paymentId, String transactionId) {
        OrderEntity order = orderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for payment ID: " + paymentId));

        order.setPaymentStatus(OrderEntity.PaymentStatus.SUCCESS);
        order.setTransactionId(transactionId);
        order.setStatus(OrderEntity.OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public OrderEntity processPaymentFailure(String paymentId, String errorMessage) {
        OrderEntity order = orderRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for payment ID: " + paymentId));

        order.setPaymentStatus(OrderEntity.PaymentStatus.FAILED);
        order.setStatus(OrderEntity.OrderStatus.FAILED);
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
    public List<OrderEntity> getOrdersByStatus(OrderEntity.OrderStatus status) {
        return orderRepository.findByStatusOrderByOrderDateDesc(status);
    }

    @Override
    public OrderEntity assignToSHG(Long orderId, Long shgUserId) {
        User shgUser = userRepository.findById(shgUserId).orElseThrow(()-> new ResourceNotFoundException("SHG didi not found"));
        if (!shgUser.getRole().equals(Role.SHG_DIDI)) {
            throw new RuntimeException("User is not an SHG Didi");
        }

        OrderEntity order = getOrderById(orderId);
        order.setAssignedSHG(shgUser);
        order.setStatus(OrderEntity.OrderStatus.PROCESSING);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public OrderEntity assignToDeliveryBoy(Long orderId, Long deliveryBoyId) {
        User deliveryBoy = userRepository.findById(deliveryBoyId).orElseThrow(()-> new ResourceNotFoundException("Delivery boy not found"));
        if (!deliveryBoy.getRole().equals(Role.DELIVERY_BOY)) {
            throw new RuntimeException("User is not a delivery boy");
        }

        OrderEntity order = getOrderById(orderId);
        order.setDeliveryBoy(deliveryBoy);
        order.setStatus(OrderEntity.OrderStatus.OUT_FOR_DELIVERY);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public List<OrderEntity> getOrdersByAssignedSHG(User shgUser) {
        return List.of();
    }

    @Override
    public List<OrderEntity> getOrdersByDeliveryBoy(User deliveryBoy) {
        return List.of();
    }
}