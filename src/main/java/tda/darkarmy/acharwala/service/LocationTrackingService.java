package tda.darkarmy.acharwala.service;

import tda.darkarmy.acharwala.dto.LocationPingRequest;
import tda.darkarmy.acharwala.dto.LocationPingResponse;

import java.util.List;

public interface LocationTrackingService {
    LocationPingResponse recordLocationPing(LocationPingRequest request);
    List<LocationPingResponse> getDidiLocationHistory();
    LocationPingResponse getLastLocationPing();
    List<LocationPingResponse> getLocationPingsForToday();
}
