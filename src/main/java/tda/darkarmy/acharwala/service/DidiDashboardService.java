package tda.darkarmy.acharwala.service;

import tda.darkarmy.acharwala.dto.DidiDashboardResponse;

public interface DidiDashboardService {
    DidiDashboardResponse getDidiDashboard();
    DidiDashboardResponse getDidiDashboardById(Long didiProfileId);
}
