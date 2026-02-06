package tda.darkarmy.acharwala.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tda.darkarmy.acharwala.dto.LocationPingRequest;
import tda.darkarmy.acharwala.dto.LocationPingResponse;
import tda.darkarmy.acharwala.exception.ResourceNotFoundException;
import tda.darkarmy.acharwala.model.DidiProfile;
import tda.darkarmy.acharwala.model.LocationPing;
import tda.darkarmy.acharwala.model.User;
import tda.darkarmy.acharwala.repository.DidiProfileRepository;
import tda.darkarmy.acharwala.repository.LocationPingRepository;
import tda.darkarmy.acharwala.service.LocationTrackingService;
import tda.darkarmy.acharwala.service.UserService;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LocationTrackingServiceImpl implements LocationTrackingService {

    private final LocationPingRepository locationPingRepository;
    private final DidiProfileRepository didiProfileRepository;
    private final UserService userService;

    @Override
    public LocationPingResponse recordLocationPing(LocationPingRequest request) {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        LocationPing ping = LocationPing.builder()
                .didiProfile(didiProfile)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .location(request.getLocation())
                .source(request.getSource() != null ? request.getSource() : "GPS")
                .accuracy(request.getAccuracy())
                .build();

        ping = locationPingRepository.save(ping);

        // Update Didi profile location
        didiProfile.setLatitude(request.getLatitude());
        didiProfile.setLongitude(request.getLongitude());
        didiProfile.setLocation(request.getLocation());
        didiProfileRepository.save(didiProfile);

        log.info("Location ping recorded for Didi ID: {} at coordinates: {}, {}", 
                didiProfile.getId(), request.getLatitude(), request.getLongitude());

        return mapToResponse(ping);
    }

    @Override
    public List<LocationPingResponse> getDidiLocationHistory() {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        List<LocationPing> pings = locationPingRepository.findByDidiProfileOrderByTimestampDesc(didiProfile);
        return pings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LocationPingResponse getLastLocationPing() {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        List<LocationPing> pings = locationPingRepository.findByDidiProfileOrderByTimestampDesc(didiProfile);
        
        if (pings.isEmpty()) {
            throw new ResourceNotFoundException("No location pings found");
        }

        return mapToResponse(pings.get(0));
    }

    @Override
    public List<LocationPingResponse> getLocationPingsForToday() {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), java.time.LocalTime.MIDNIGHT);
        List<LocationPing> pings = locationPingRepository.findByDidiProfileAndTimestampAfter(didiProfile, startOfDay);
        
        return pings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private LocationPingResponse mapToResponse(LocationPing ping) {
        return LocationPingResponse.builder()
                .id(ping.getId())
                .latitude(ping.getLatitude())
                .longitude(ping.getLongitude())
                .location(ping.getLocation())
                .timestamp(ping.getTimestamp())
                .source(ping.getSource())
                .accuracy(ping.getAccuracy())
                .build();
    }
}
