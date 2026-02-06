package tda.darkarmy.acharwala.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tda.darkarmy.acharwala.model.OrderEntity;
import tda.darkarmy.acharwala.model.User;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    List<OrderEntity> findByUserOrderByOrderDateDesc(User user);

    Page<OrderEntity> findByUser(User user, Pageable pageable);

    List<OrderEntity> findAllByOrderByOrderDateDesc();

    List<OrderEntity> findByStatusOrderByOrderDateDesc(OrderEntity.OrderStatus status);

    Optional<OrderEntity> findByPaymentId(String paymentId);

    List<OrderEntity> findByAssignedSHG(User assignedSHG);

    List<OrderEntity> findByDeliveryBoy(User deliveryBoy);
}
