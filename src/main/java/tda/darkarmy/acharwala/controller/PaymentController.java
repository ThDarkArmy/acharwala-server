// package tda.darkarmy.acharwala.controller;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;
// import tda.darkarmy.acharwala.dto.PaymentRequest;
// import tda.darkarmy.acharwala.dto.PaymentResponse;
// import tda.darkarmy.acharwala.dto.PaymentVerificationRequest;
// import tda.darkarmy.acharwala.service.PaymentService;

// PAYMENT MODULE COMMENTED OUT - TO BE IMPLEMENTED LATER
/*
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment APIs", description = "Payment gateway integration endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Initiate payment", description = "Create a payment order with Razorpay")
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.initiatePayment(paymentRequest));
    }

    @Operation(summary = "Verify payment", description = "Verify payment signature and update payment status")
    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(@Valid @RequestBody PaymentVerificationRequest verificationRequest) {
        return ResponseEntity.ok(paymentService.verifyPayment(verificationRequest));
    }

    @Operation(summary = "Get payment by order ID", description = "Retrieve payment details for an order")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @Operation(summary = "Get payment by ID", description = "Retrieve payment details by payment ID")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @Operation(summary = "Refund payment", description = "Process refund for a payment (Admin only)")
    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Long paymentId,
            @RequestParam(required = false) Double amount) {
        // If amount is not provided, refund full amount
        if (amount == null) {
            PaymentResponse payment = paymentService.getPaymentById(paymentId);
            amount = payment.getAmount().doubleValue();
        }
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, amount));
    }
}
*/
