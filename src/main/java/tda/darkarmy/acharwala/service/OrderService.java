package tda.darkarmy.acharwala.service;

import tda.darkarmy.acharwala.dto.OrderRequest;
import tda.darkarmy.acharwala.model.OrderEntity;
import tda.darkarmy.acharwala.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderEntity createOrder(OrderRequest orderRequest);
    OrderEntity getOrderById(Long orderId);
    OrderEntity getOrderByOrderNumber(String orderNumber);
    List<OrderEntity> getUserOrders();
    Page<OrderEntity> getUserOrdersPaginated(Pageable pageable);
    List<OrderEntity> getAllOrders();
    Page<OrderEntity> getAllOrdersPaginated(Pageable pageable);
    OrderEntity updateOrderStatus(Long orderId, OrderEntity.OrderStatus status);
    OrderEntity cancelOrder(Long orderId);
    OrderEntity processPaymentSuccess(String paymentId, String transactionId);
    OrderEntity processPaymentFailure(String paymentId, String errorMessage);
    List<OrderEntity> getOrdersByStatus(OrderEntity.OrderStatus status);
    OrderEntity assignToSHG(Long orderId, Long shgUserId); // Changed to accept ID

    OrderEntity assignToDeliveryBoy(Long orderId, Long deliveryBoyId); // Changed to accept ID

    List<OrderEntity> getOrdersByAssignedSHG(User shgUser);
    List<OrderEntity> getOrdersByDeliveryBoy(User deliveryBoy);
}