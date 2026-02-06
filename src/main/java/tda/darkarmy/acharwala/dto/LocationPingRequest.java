package tda.darkarmy.acharwala.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationPingRequest {
    private Double latitude;
    private Double longitude;
    private String location;
    private String source; // GPS, MANUAL, etc.
    private String accuracy;
}
