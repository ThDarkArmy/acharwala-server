package tda.darkarmy.acharwala.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tda.darkarmy.acharwala.dto.LocationPingRequest;
import tda.darkarmy.acharwala.dto.LocationPingResponse;
import tda.darkarmy.acharwala.service.LocationTrackingService;

import java.util.List;

@RestController
@RequestMapping("/api/didi/location")
@RequiredArgsConstructor
@Tag(name = "Didi Location Tracking", description = "APIs for tracking Didi GPS location pings")
public class DidiLocationController {

    private final LocationTrackingService locationTrackingService;

    @Operation(summary = "Record location ping", description = "Record Didi's current GPS location")
    @PostMapping("/ping")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<LocationPingResponse> recordLocationPing(@RequestBody LocationPingRequest request) {
        return ResponseEntity.ok(locationTrackingService.recordLocationPing(request));
    }

    @Operation(summary = "Get location history", description = "Get Didi's location ping history")
    @GetMapping("/history")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<List<LocationPingResponse>> getDidiLocationHistory() {
        return ResponseEntity.ok(locationTrackingService.getDidiLocationHistory());
    }

    @Operation(summary = "Get last location", description = "Get Didi's last recorded location")
    @GetMapping("/last")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<LocationPingResponse> getLastLocationPing() {
        return ResponseEntity.ok(locationTrackingService.getLastLocationPing());
    }

    @Operation(summary = "Get today's locations", description = "Get all location pings recorded today")
    @GetMapping("/today")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<List<LocationPingResponse>> getLocationPingsForToday() {
        return ResponseEntity.ok(locationTrackingService.getLocationPingsForToday());
    }
}
