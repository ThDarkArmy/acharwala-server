package tda.darkarmy.acharwala.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DidiDashboardResponse {

    private Long didiProfileId;

    private String didiName;

    private String location;

    // Performance Metrics
    private BigDecimal totalEarnings;

    private BigDecimal averageRating;

    private Integer totalOrders;

    private Integer totalSales;

    // Order Information
    private Integer pendingOrders;

    private Integer completedOrders;

    private Integer cancelledOrders;

    // Training Status
    private String trainingStatus;

    private Integer trainingCompletionPercentage;

    // Recent Orders
    private List<OrderSummary> recentOrders;

    // Location Info
    private Double latitude;

    private Double longitude;

    private String lastLocationUpdate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderSummary {
        private Long orderId;
        private String orderNumber;
        private String status;
        private BigDecimal amount;
        private String customerName;
    }
}
