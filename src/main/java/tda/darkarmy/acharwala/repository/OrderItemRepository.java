package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tda.darkarmy.acharwala.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
