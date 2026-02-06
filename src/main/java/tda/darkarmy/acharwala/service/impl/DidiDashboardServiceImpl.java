package tda.darkarmy.acharwala.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tda.darkarmy.acharwala.dto.DidiDashboardResponse;
import tda.darkarmy.acharwala.exception.ResourceNotFoundException;
import tda.darkarmy.acharwala.model.DidiProfile;
import tda.darkarmy.acharwala.model.LocationPing;
import tda.darkarmy.acharwala.model.OrderEntity;
import tda.darkarmy.acharwala.model.TrainingProgress;
import tda.darkarmy.acharwala.model.User;
import tda.darkarmy.acharwala.repository.DidiProfileRepository;
import tda.darkarmy.acharwala.repository.LocationPingRepository;
import tda.darkarmy.acharwala.repository.OrderRepository;
import tda.darkarmy.acharwala.repository.TrainingProgressRepository;
import tda.darkarmy.acharwala.service.DidiDashboardService;
import tda.darkarmy.acharwala.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DidiDashboardServiceImpl implements DidiDashboardService {

    private final DidiProfileRepository didiProfileRepository;
    private final OrderRepository orderRepository;
    private final TrainingProgressRepository trainingProgressRepository;
    private final LocationPingRepository locationPingRepository;
    private final UserService userService;

    @Override
    public DidiDashboardResponse getDidiDashboard() {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        return buildDashboard(didiProfile);
    }

    @Override
    public DidiDashboardResponse getDidiDashboardById(Long didiProfileId) {
        DidiProfile didiProfile = didiProfileRepository.findById(didiProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        return buildDashboard(didiProfile);
    }

    private DidiDashboardResponse buildDashboard(DidiProfile didiProfile) {
        // Get orders assigned to this Didi
        List<OrderEntity> assignedOrders = orderRepository.findByAssignedSHG(didiProfile.getUser());

        // Count orders by status
        int pendingOrders = (int) assignedOrders.stream()
                .filter(o -> o.getStatus() == OrderEntity.OrderStatus.PENDING)
                .count();
        int completedOrders = (int) assignedOrders.stream()
                .filter(o -> o.getStatus() == OrderEntity.OrderStatus.DELIVERED)
                .count();
        int cancelledOrders = (int) assignedOrders.stream()
                .filter(o -> o.getStatus() == OrderEntity.OrderStatus.CANCELLED)
                .count();

        // Get recent orders (last 5)
        List<DidiDashboardResponse.OrderSummary> recentOrders = assignedOrders.stream()
                .limit(5)
                .map(order -> DidiDashboardResponse.OrderSummary.builder()
                        .orderId(order.getId())
                        .orderNumber(order.getOrderNumber())
                        .status(order.getStatus().toString())
                        .amount(order.getFinalAmount())
                        .customerName(order.getUser().getName())
                        .build())
                .collect(Collectors.toList());

        // Get training completion percentage
        List<TrainingProgress> trainingProgress = trainingProgressRepository
                .findByDidiProfileOrderByCreatedAtDesc(didiProfile);
        
        int trainingCompletionPercentage = 0;
        if (!trainingProgress.isEmpty()) {
            long completedCount = trainingProgress.stream()
                    .filter(p -> p.getStatus() == TrainingProgress.ProgressStatus.COMPLETED)
                    .count();
            trainingCompletionPercentage = (int) ((completedCount * 100) / trainingProgress.size());
        }

        // Get last location ping
        List<LocationPing> locationPings = locationPingRepository.findByDidiProfileOrderByTimestampDesc(didiProfile);
        String lastLocationUpdate = "N/A";
        if (!locationPings.isEmpty()) {
            LocalDateTime lastPingTime = locationPings.get(0).getTimestamp();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lastLocationUpdate = lastPingTime.format(formatter);
        }

        return DidiDashboardResponse.builder()
                .didiProfileId(didiProfile.getId())
                .didiName(didiProfile.getUser().getName())
                .location(didiProfile.getLocation())
                .totalEarnings(didiProfile.getTotalEarnings())
                .averageRating(didiProfile.getAverageRating())
                .totalOrders(didiProfile.getTotalOrders())
                .totalSales(didiProfile.getTotalSales())
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .trainingStatus(didiProfile.getTrainingStatus().toString())
                .trainingCompletionPercentage(trainingCompletionPercentage)
                .recentOrders(recentOrders)
                .latitude(didiProfile.getLatitude())
                .longitude(didiProfile.getLongitude())
                .lastLocationUpdate(lastLocationUpdate)
                .build();
    }
}
