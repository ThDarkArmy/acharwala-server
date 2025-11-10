package tda.darkarmy.acharwala.service;

import tda.darkarmy.acharwala.dto.OrderRequest;
import tda.darkarmy.acharwala.model.Order;
import tda.darkarmy.acharwala.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderRequest orderRequest);
    Order getOrderById(Long orderId);
    Order getOrderByOrderNumber(String orderNumber);
    List<Order> getUserOrders();
    Page<Order> getUserOrdersPaginated(Pageable pageable);
    List<Order> getAllOrders();
    Page<Order> getAllOrdersPaginated(Pageable pageable);
    Order updateOrderStatus(Long orderId, Order.OrderStatus status);
    Order cancelOrder(Long orderId);
    Order processPaymentSuccess(String paymentId, String transactionId);
    Order processPaymentFailure(String paymentId, String errorMessage);
    List<Order> getOrdersByStatus(Order.OrderStatus status);
    Order assignToSHG(Long orderId, Long shgUserId); // Changed to accept ID

    Order assignToDeliveryBoy(Long orderId, Long deliveryBoyId); // Changed to accept ID

    List<Order> getOrdersByAssignedSHG(User shgUser);
    List<Order> getOrdersByDeliveryBoy(User deliveryBoy);
}