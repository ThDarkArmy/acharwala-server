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
public class LocationPingResponse {
    private Long id;
    private Double latitude;
    private Double longitude;
    private String location;
    private LocalDateTime timestamp;
    private String source;
    private String accuracy;
}
