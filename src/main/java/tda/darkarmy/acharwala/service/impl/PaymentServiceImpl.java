// package tda.darkarmy.acharwala.service.impl;

// import com.razorpay.Order;
// import com.razorpay.Payment;
// import com.razorpay.RazorpayClient;
// import com.razorpay.RazorpayException;
// import com.razorpay.Refund;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import tda.darkarmy.acharwala.dto.PaymentRequest;
// import tda.darkarmy.acharwala.dto.PaymentResponse;
// import tda.darkarmy.acharwala.dto.PaymentVerificationRequest;
// import tda.darkarmy.acharwala.exception.ResourceNotFoundException;
// import tda.darkarmy.acharwala.model.OrderEntity;
// import tda.darkarmy.acharwala.model.PaymentEntity;
// import tda.darkarmy.acharwala.repository.OrderRepository;
// import tda.darkarmy.acharwala.repository.PaymentRepository;
// import tda.darkarmy.acharwala.service.OrderService;
// import tda.darkarmy.acharwala.service.PaymentService;

// import javax.crypto.Mac;
// import javax.crypto.spec.SecretKeySpec;
// import java.math.BigDecimal;
// import java.nio.charset.StandardCharsets;
// import java.time.LocalDateTime;
// import java.util.Base64;

// PAYMENT MODULE COMMENTED OUT - TO BE IMPLEMENTED LATER
/*
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public PaymentResponse initiatePayment(PaymentRequest paymentRequest) {
        OrderEntity order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getPaymentStatus() == OrderEntity.PaymentStatus.SUCCESS) {
            throw new RuntimeException("Order is already paid");
        }

        try {
            // Create Razorpay Order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue()); // Convert to paise
            orderRequest.put("currency", paymentRequest.getCurrency());
            orderRequest.put("receipt", "order_" + order.getOrderNumber());
            orderRequest.put("notes", new JSONObject()
                    .put("orderId", order.getId().toString())
                    .put("orderNumber", order.getOrderNumber()));

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            // Create Payment entity
            PaymentEntity payment = PaymentEntity.builder()
                    .order(order)
                    .amount(paymentRequest.getAmount())
                    .currency(paymentRequest.getCurrency())
                    .status(PaymentEntity.PaymentStatus.PENDING)
                    .paymentMethod(paymentRequest.getPaymentMethod())
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .paymentId("pay_" + System.currentTimeMillis())
                    .build();

            payment = paymentRepository.save(payment);

            // Update order with payment ID
            order.setPaymentId(razorpayOrder.get("id"));
            orderRepository.save(order);

            return PaymentResponse.builder()
                    .id(payment.getId())
                    .paymentId(payment.getPaymentId())
                    .orderId(order.getId())
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .status(payment.getStatus())
                    .paymentMethod(payment.getPaymentMethod())
                    .orderIdForRazorpay(razorpayOrder.get("id"))
                    .keyId(razorpayKeyId)
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .createdAt(payment.getCreatedAt())
                    .build();

        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order: {}", e.getMessage());
            throw new RuntimeException("Failed to initiate payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse verifyPayment(PaymentVerificationRequest verificationRequest) {
        OrderEntity order = orderRepository.findById(verificationRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        PaymentEntity payment = paymentRepository.findByRazorpayOrderId(verificationRequest.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        try {
            // Verify signature
            boolean isValidSignature = verifySignature(
                    verificationRequest.getRazorpayOrderId(),
                    verificationRequest.getRazorpayPaymentId(),
                    verificationRequest.getRazorpaySignature()
            );

            if (!isValidSignature) {
                payment.setStatus(PaymentEntity.PaymentStatus.FAILED);
                payment.setFailureReason("Invalid signature");
                paymentRepository.save(payment);
                throw new RuntimeException("Invalid payment signature");
            }

            // Fetch payment details from Razorpay
            Payment razorpayPayment = razorpayClient.payments.fetch(verificationRequest.getRazorpayPaymentId());

            // Update payment entity
            payment.setRazorpayPaymentId(razorpayPayment.get("id"));
            payment.setRazorpaySignature(verificationRequest.getRazorpaySignature());
            payment.setTransactionId(razorpayPayment.has("acquirer_data") && 
                    razorpayPayment.getJSONObject("acquirer_data").has("rrn") ?
                    razorpayPayment.getJSONObject("acquirer_data").getString("rrn") : null);

            if ("authorized".equals(razorpayPayment.get("status")) || 
                "captured".equals(razorpayPayment.get("status"))) {
                payment.setStatus(PaymentEntity.PaymentStatus.SUCCESS);
                payment.setCompletedAt(LocalDateTime.now());
                
                // Update order payment status
                orderService.processPaymentSuccess(
                        order.getPaymentId(),
                        payment.getTransactionId() != null ? payment.getTransactionId() : payment.getRazorpayPaymentId()
                );
            } else {
                payment.setStatus(PaymentEntity.PaymentStatus.FAILED);
                payment.setFailureReason(razorpayPayment.has("error_description") ?
                        razorpayPayment.getString("error_description") : "Payment failed");
                
                orderService.processPaymentFailure(
                        order.getPaymentId(),
                        payment.getFailureReason()
                );
            }

            payment = paymentRepository.save(payment);

            return PaymentResponse.builder()
                    .id(payment.getId())
                    .paymentId(payment.getPaymentId())
                    .orderId(order.getId())
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .status(payment.getStatus())
                    .paymentMethod(payment.getPaymentMethod())
                    .razorpayOrderId(payment.getRazorpayOrderId())
                    .razorpayPaymentId(payment.getRazorpayPaymentId())
                    .createdAt(payment.getCreatedAt())
                    .completedAt(payment.getCompletedAt())
                    .failureReason(payment.getFailureReason())
                    .build();

        } catch (RazorpayException e) {
            log.error("Error verifying payment: {}", e.getMessage());
            payment.setStatus(PaymentEntity.PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            throw new RuntimeException("Failed to verify payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        PaymentEntity payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for this order"));

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse refundPayment(Long paymentId, Double amount) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentEntity.PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only successful payments can be refunded");
        }

        try {
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(100)).longValue());
            refundRequest.put("speed", "normal");

            Refund refund = razorpayClient.payments.refund(payment.getRazorpayPaymentId(), refundRequest);

            payment.setStatus(PaymentEntity.PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            // Update order status
            OrderEntity order = payment.getOrder();
            order.setPaymentStatus(OrderEntity.PaymentStatus.REFUNDED);
            order.setStatus(OrderEntity.OrderStatus.REFUNDED);
            orderRepository.save(order);

            return mapToResponse(payment);

        } catch (RazorpayException e) {
            log.error("Error processing refund: {}", e.getMessage());
            throw new RuntimeException("Failed to process refund: " + e.getMessage());
        }
    }

    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = Base64.getEncoder().encodeToString(hash);
            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying signature: {}", e.getMessage());
            return false;
        }
    }

    private PaymentResponse mapToResponse(PaymentEntity payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .failureReason(payment.getFailureReason())
                .build();
    }
}
*/
