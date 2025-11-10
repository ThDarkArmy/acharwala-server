package tda.darkarmy.acharwala.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tda.darkarmy.acharwala.model.Order;
import tda.darkarmy.acharwala.model.User;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserOrderByOrderDateDesc(User user);

    Page<Order> findByUser(User user, Pageable pageable);

    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);

    Optional<Order> findByPaymentId(String paymentId);
}
