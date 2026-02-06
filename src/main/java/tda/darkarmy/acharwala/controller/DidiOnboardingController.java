package tda.darkarmy.acharwala.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tda.darkarmy.acharwala.dto.DidiOnboardingRequest;
import tda.darkarmy.acharwala.dto.DidiProfileResponse;
import tda.darkarmy.acharwala.dto.DidiApprovalRequest;
import tda.darkarmy.acharwala.service.DidiOnboardingService;

import java.util.List;

@RestController
@RequestMapping("/api/didi/onboarding")
@RequiredArgsConstructor
@Tag(name = "Didi Onboarding", description = "APIs for SHG Didi registration and approval workflow")
public class DidiOnboardingController {

    private final DidiOnboardingService didiOnboardingService;

    @Operation(summary = "Register as Didi", description = "SHG Didi registration with Aadhaar, bank details, and location")
    @PostMapping("/register")
    public ResponseEntity<DidiProfileResponse> registerDidi(@Valid @ModelAttribute DidiOnboardingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(didiOnboardingService.registerDidi(request));
    }

    @Operation(summary = "Get my profile", description = "Get current logged-in Didi's profile")
    @GetMapping("/profile")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<DidiProfileResponse> getDidiProfile() {
        return ResponseEntity.ok(didiOnboardingService.getDidiProfile());
    }

    @Operation(summary = "Get Didi profile by ID", description = "Get specific Didi's profile (Admin only)")
    @GetMapping("/profile/{didiProfileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DidiProfileResponse> getDidiProfileById(@PathVariable Long didiProfileId) {
        return ResponseEntity.ok(didiOnboardingService.getDidiProfileById(didiProfileId));
    }

    @Operation(summary = "Get pending approvals", description = "Get all pending Didi registrations (Admin only)")
    @GetMapping("/approvals/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DidiProfileResponse>> getPendingApprovals() {
        return ResponseEntity.ok(didiOnboardingService.getPendingApprovals());
    }

    @Operation(summary = "Approve Didi", description = "Admin approves a Didi registration")
    @PostMapping("/approvals/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DidiProfileResponse> approveDidi(@Valid @RequestBody DidiApprovalRequest request) {
        return ResponseEntity.ok(didiOnboardingService.approveDidi(request));
    }

    @Operation(summary = "Reject Didi", description = "Admin rejects a Didi registration with reason")
    @PostMapping("/approvals/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DidiProfileResponse> rejectDidi(@Valid @RequestBody DidiApprovalRequest request) {
        return ResponseEntity.ok(didiOnboardingService.rejectDidi(request));
    }

    @Operation(summary = "Get all approved Didis", description = "Get list of all approved Didis (Admin only)")
    @GetMapping("/approved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DidiProfileResponse>> getAllApprovedDidis() {
        return ResponseEntity.ok(didiOnboardingService.getAllApprovedDidis());
    }

    @Operation(summary = "Update Didi profile", description = "Update Didi's profile information")
    @PutMapping("/profile/update")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<DidiProfileResponse> updateDidiProfile(@Valid @ModelAttribute DidiOnboardingRequest request) {
        return ResponseEntity.ok(didiOnboardingService.updateDidiProfile(request));
    }

    @Operation(summary = "Suspend Didi", description = "Admin suspends a Didi account")
    @PutMapping("/{didiProfileId}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> suspendDidi(
            @PathVariable Long didiProfileId,
            @RequestParam String reason) {
        return ResponseEntity.ok(didiOnboardingService.suspendDidi(didiProfileId, reason));
    }

    @Operation(summary = "Reactivate Didi", description = "Admin reactivates a suspended Didi")
    @PutMapping("/{didiProfileId}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> reactivateDidi(@PathVariable Long didiProfileId) {
        return ResponseEntity.ok(didiOnboardingService.reactivateDidi(didiProfileId));
    }
}
