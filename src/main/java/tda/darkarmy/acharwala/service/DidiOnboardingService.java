package tda.darkarmy.acharwala.service;

import tda.darkarmy.acharwala.dto.DidiOnboardingRequest;
import tda.darkarmy.acharwala.dto.DidiProfileResponse;
import tda.darkarmy.acharwala.dto.DidiApprovalRequest;
import tda.darkarmy.acharwala.model.DidiProfile;

import java.util.List;
import java.util.Optional;

public interface DidiOnboardingService {
    DidiProfileResponse registerDidi(DidiOnboardingRequest request);
    DidiProfileResponse getDidiProfile();
    DidiProfileResponse getDidiProfileById(Long didiProfileId);
    List<DidiProfileResponse> getPendingApprovals();
    DidiProfileResponse approveDidi(DidiApprovalRequest approvalRequest);
    DidiProfileResponse rejectDidi(DidiApprovalRequest approvalRequest);
    List<DidiProfileResponse> getAllApprovedDidis();
    DidiProfileResponse updateDidiProfile(DidiOnboardingRequest request);
    String suspendDidi(Long didiProfileId, String reason);
    String reactivateDidi(Long didiProfileId);
}
