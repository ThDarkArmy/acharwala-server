package tda.darkarmy.acharwala.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tda.darkarmy.acharwala.dto.DidiOnboardingRequest;
import tda.darkarmy.acharwala.dto.DidiProfileResponse;
import tda.darkarmy.acharwala.dto.DidiApprovalRequest;
import tda.darkarmy.acharwala.exception.ResourceNotFoundException;
import tda.darkarmy.acharwala.model.DidiProfile;
import tda.darkarmy.acharwala.model.User;
import tda.darkarmy.acharwala.repository.DidiProfileRepository;
import tda.darkarmy.acharwala.service.DidiOnboardingService;
import tda.darkarmy.acharwala.service.UserService;
import tda.darkarmy.acharwala.util.FileStorageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DidiOnboardingServiceImpl implements DidiOnboardingService {

    private final DidiProfileRepository didiProfileRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Override
    public DidiProfileResponse registerDidi(DidiOnboardingRequest request) {
        User user = userService.getLoggedInUser();

        // Check if Didi profile already exists
        if (didiProfileRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Didi profile already exists for this user");
        }

        // Check if Aadhaar already registered
        if (didiProfileRepository.findByAadhaarNumber(request.getAadhaarNumber()).isPresent()) {
            throw new RuntimeException("Aadhaar number already registered");
        }

        // Store Aadhaar image if provided
        String aadhaarImageUrl = null;
        if (request.getAadhaarImage() != null && !request.getAadhaarImage().isEmpty()) {
            aadhaarImageUrl = fileStorageService.storeFile(request.getAadhaarImage());
        }

        DidiProfile profile = DidiProfile.builder()
                .user(user)
                .aadhaarNumber(request.getAadhaarNumber())
                .aadharImageUrl(aadhaarImageUrl)
                .bankAccountNumber(request.getBankAccountNumber())
                .bankIFSC(request.getBankIFSC())
                .bankName(request.getBankName())
                .accountHolderName(request.getAccountHolderName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .location(request.getLocation())
                .approvalStatus(DidiProfile.ApprovalStatus.PENDING)
                .trainingStatus(DidiProfile.TrainingStatus.NOT_STARTED)
                .build();

        profile = didiProfileRepository.save(profile);
        log.info("Didi registered with ID: {} - Awaiting approval", profile.getId());

        return mapToResponse(profile);
    }

    @Override
    public DidiProfileResponse getDidiProfile() {
        User user = userService.getLoggedInUser();
        DidiProfile profile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));
        return mapToResponse(profile);
    }

    @Override
    public DidiProfileResponse getDidiProfileById(Long didiProfileId) {
        DidiProfile profile = didiProfileRepository.findById(didiProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));
        return mapToResponse(profile);
    }

    @Override
    public List<DidiProfileResponse> getPendingApprovals() {
        List<DidiProfile> pendingProfiles = didiProfileRepository
                .findByApprovalStatus(DidiProfile.ApprovalStatus.PENDING);
        return pendingProfiles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DidiProfileResponse approveDidi(DidiApprovalRequest approvalRequest) {
        DidiProfile profile = didiProfileRepository.findById(approvalRequest.getDidiProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        profile.setApprovalStatus(DidiProfile.ApprovalStatus.APPROVED);
        profile.setApprovedAt(LocalDateTime.now());
        profile.setTrainingStatus(DidiProfile.TrainingStatus.NOT_STARTED);

        profile = didiProfileRepository.save(profile);
        log.info("Didi approved with ID: {}", profile.getId());

        return mapToResponse(profile);
    }

    @Override
    public DidiProfileResponse rejectDidi(DidiApprovalRequest approvalRequest) {
        DidiProfile profile = didiProfileRepository.findById(approvalRequest.getDidiProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        profile.setApprovalStatus(DidiProfile.ApprovalStatus.REJECTED);
        profile.setRejectionReason(approvalRequest.getRejectionReason());

        profile = didiProfileRepository.save(profile);
        log.info("Didi rejected with ID: {}", profile.getId());

        return mapToResponse(profile);
    }

    @Override
    public List<DidiProfileResponse> getAllApprovedDidis() {
        List<DidiProfile> approvedProfiles = didiProfileRepository
                .findByApprovalStatus(DidiProfile.ApprovalStatus.APPROVED);
        return approvedProfiles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DidiProfileResponse updateDidiProfile(DidiOnboardingRequest request) {
        User user = userService.getLoggedInUser();
        DidiProfile profile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        if (request.getAadhaarNumber() != null) {
            profile.setAadhaarNumber(request.getAadhaarNumber());
        }
        if (request.getAadhaarImage() != null && !request.getAadhaarImage().isEmpty()) {
            if (profile.getAadharImageUrl() != null) {
                fileStorageService.deleteFile(profile.getAadharImageUrl());
            }
            String newImageUrl = fileStorageService.storeFile(request.getAadhaarImage());
            profile.setAadharImageUrl(newImageUrl);
        }
        if (request.getBankAccountNumber() != null) {
            profile.setBankAccountNumber(request.getBankAccountNumber());
        }
        if (request.getBankIFSC() != null) {
            profile.setBankIFSC(request.getBankIFSC());
        }
        if (request.getBankName() != null) {
            profile.setBankName(request.getBankName());
        }
        if (request.getAccountHolderName() != null) {
            profile.setAccountHolderName(request.getAccountHolderName());
        }
        if (request.getLatitude() != null) {
            profile.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            profile.setLongitude(request.getLongitude());
        }
        if (request.getLocation() != null) {
            profile.setLocation(request.getLocation());
        }

        profile = didiProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Override
    public String suspendDidi(Long didiProfileId, String reason) {
        DidiProfile profile = didiProfileRepository.findById(didiProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        profile.setApprovalStatus(DidiProfile.ApprovalStatus.SUSPENDED);
        profile.setRejectionReason(reason);
        didiProfileRepository.save(profile);

        log.info("Didi suspended with ID: {} - Reason: {}", didiProfileId, reason);
        return "Didi has been suspended";
    }

    @Override
    public String reactivateDidi(Long didiProfileId) {
        DidiProfile profile = didiProfileRepository.findById(didiProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        profile.setApprovalStatus(DidiProfile.ApprovalStatus.APPROVED);
        profile.setRejectionReason(null);
        didiProfileRepository.save(profile);

        log.info("Didi reactivated with ID: {}", didiProfileId);
        return "Didi has been reactivated";
    }

    private DidiProfileResponse mapToResponse(DidiProfile profile) {
        return DidiProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .userName(profile.getUser().getName())
                .userEmail(profile.getUser().getEmail())
                .phoneNumber(profile.getUser().getPhoneNumber())
                .aadhaarNumber(profile.getAadhaarNumber())
                .aadharImageUrl(profile.getAadharImageUrl())
                .bankAccountNumber(profile.getBankAccountNumber())
                .bankIFSC(profile.getBankIFSC())
                .bankName(profile.getBankName())
                .accountHolderName(profile.getAccountHolderName())
                .latitude(profile.getLatitude())
                .longitude(profile.getLongitude())
                .location(profile.getLocation())
                .approvalStatus(profile.getApprovalStatus())
                .rejectionReason(profile.getRejectionReason())
                .trainingStatus(profile.getTrainingStatus())
                .trainingCompletedAt(profile.getTrainingCompletedAt())
                .totalEarnings(profile.getTotalEarnings())
                .averageRating(profile.getAverageRating())
                .totalOrders(profile.getTotalOrders())
                .totalSales(profile.getTotalSales())
                .createdAt(profile.getCreatedAt())
                .approvedAt(profile.getApprovedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
