package tda.darkarmy.acharwala.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "location_pings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationPing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "didi_profile_id", nullable = false)
    private DidiProfile didiProfile;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String location; // Address/area name

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false)
    private String source; // GPS, MANUAL, etc.

    private String accuracy; // GPS accuracy

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
