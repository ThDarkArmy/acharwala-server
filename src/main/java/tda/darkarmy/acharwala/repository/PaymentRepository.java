// PAYMENT MODULE COMMENTED OUT - TO BE IMPLEMENTED LATER
/*
package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tda.darkarmy.acharwala.model.OrderEntity;
import tda.darkarmy.acharwala.model.PaymentEntity;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByPaymentId(String paymentId);
    Optional<PaymentEntity> findByRazorpayPaymentId(String razorpayPaymentId);
    Optional<PaymentEntity> findByOrder(OrderEntity order);
    Optional<PaymentEntity> findByRazorpayOrderId(String razorpayOrderId);
}
*/
