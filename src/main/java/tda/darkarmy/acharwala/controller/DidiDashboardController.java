package tda.darkarmy.acharwala.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tda.darkarmy.acharwala.dto.DidiDashboardResponse;
import tda.darkarmy.acharwala.service.DidiDashboardService;

@RestController
@RequestMapping("/api/didi/dashboard")
@RequiredArgsConstructor
@Tag(name = "Didi Dashboard", description = "APIs for Didi sales dashboard and metrics")
public class DidiDashboardController {

    private final DidiDashboardService didiDashboardService;

    @Operation(summary = "Get my dashboard", description = "Get current Didi's dashboard with metrics and orders")
    @GetMapping
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<DidiDashboardResponse> getDidiDashboard() {
        return ResponseEntity.ok(didiDashboardService.getDidiDashboard());
    }

    @Operation(summary = "Get Didi dashboard by ID", description = "Get specific Didi's dashboard (Admin only)")
    @GetMapping("/{didiProfileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DidiDashboardResponse> getDidiDashboardById(@PathVariable Long didiProfileId) {
        return ResponseEntity.ok(didiDashboardService.getDidiDashboardById(didiProfileId));
    }
}
