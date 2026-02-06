package tda.darkarmy.acharwala.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tda.darkarmy.acharwala.dto.AssignOrderRequest;
import tda.darkarmy.acharwala.dto.OrderRequest;
import tda.darkarmy.acharwala.model.OrderEntity;
import tda.darkarmy.acharwala.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order APIs", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order", description = "Allows a user to create a new order")
    @ApiResponse(responseCode = "200", description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = OrderEntity.class)))
    @PostMapping
    public ResponseEntity<OrderEntity> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @Operation(summary = "Get order by ID", description = "Fetch a single order using its ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderEntity> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @Operation(summary = "Get order by number", description = "Fetch a single order using its order number")
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderEntity> getOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByOrderNumber(orderNumber));
    }

    @Operation(summary = "Get current user's orders", description = "Fetch all orders placed by the logged-in user")
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderEntity>> getUserOrders() {
        return ResponseEntity.ok(orderService.getUserOrders());
    }

    @Operation(summary = "Get paginated current user's orders", description = "Fetch paginated orders for the logged-in user")
    @GetMapping("/my-orders/paginated")
    public ResponseEntity<Page<OrderEntity>> getUserOrdersPaginated(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "orderDate") String sortBy,
            @Parameter(description = "Sort direction: asc/desc") @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(orderService.getUserOrdersPaginated(pageable));
    }

    @Operation(summary = "Get all orders (Admin)", description = "Admin-only: fetch all orders")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderEntity>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Get all orders paginated (Admin)", description = "Admin-only: fetch all orders with pagination")
    @GetMapping("/all/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderEntity>> getAllOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(orderService.getAllOrdersPaginated(pageable));
    }

    @Operation(summary = "Update order status", description = "Admin/SHG can update the status of an order")
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHG_DIDI')")
    public ResponseEntity<OrderEntity> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderEntity.OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @Operation(summary = "Cancel an order", description = "User can cancel their order")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderEntity> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @Operation(summary = "Process payment success", description = "Webhook: update order after successful payment")
    @PostMapping("/payment/success")
    public ResponseEntity<OrderEntity> processPaymentSuccess(
            @RequestParam String paymentId,
            @RequestParam String transactionId) {
        return ResponseEntity.ok(orderService.processPaymentSuccess(paymentId, transactionId));
    }

    @Operation(summary = "Process payment failure", description = "Webhook: update order after failed payment")
    @PostMapping("/payment/failure")
    public ResponseEntity<OrderEntity> processPaymentFailure(
            @RequestParam String paymentId,
            @RequestParam String errorMessage) {
        return ResponseEntity.ok(orderService.processPaymentFailure(paymentId, errorMessage));
    }

    @Operation(summary = "Get orders by status", description = "Admin/SHG can filter orders by status")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHG_DIDI')")
    public ResponseEntity<List<OrderEntity>> getOrdersByStatus(@PathVariable OrderEntity.OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @Operation(summary = "Assign order to SHG", description = "Admin can assign order to SHG")
    @PostMapping("/{orderId}/assign/shg")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderEntity> assignToSHG(
            @PathVariable Long orderId,
            @Valid @RequestBody AssignOrderRequest request) {
        return ResponseEntity.ok(orderService.assignToSHG(orderId, request.getUserId()));
    }

    @Operation(summary = "Assign order to delivery boy", description = "Admin/SHG can assign order to a delivery boy")
    @PostMapping("/{orderId}/assign/delivery")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHG_DIDI')")
    public ResponseEntity<OrderEntity> assignToDeliveryBoy(
            @PathVariable Long orderId,
            @Valid @RequestBody AssignOrderRequest request) {
        return ResponseEntity.ok(orderService.assignToDeliveryBoy(orderId, request.getUserId()));
    }

    @Operation(summary = "Get SHG orders", description = "Fetch orders assigned to the logged-in SHG")
    @GetMapping("/shg/my-orders")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<List<OrderEntity>> getSHGOrders() {
        return ResponseEntity.ok(orderService.getOrdersByStatus(OrderEntity.OrderStatus.PROCESSING));
    }

    @Operation(summary = "Get Delivery Boy orders", description = "Fetch orders assigned to the logged-in Delivery Boy")
    @GetMapping("/delivery/my-orders")
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public ResponseEntity<List<OrderEntity>> getDeliveryBoyOrders() {
        return ResponseEntity.ok(orderService.getOrdersByStatus(OrderEntity.OrderStatus.OUT_FOR_DELIVERY));
    }
}
