package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tda.darkarmy.acharwala.model.LocationPing;
import tda.darkarmy.acharwala.model.DidiProfile;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationPingRepository extends JpaRepository<LocationPing, Long> {
    List<LocationPing> findByDidiProfileOrderByTimestampDesc(DidiProfile didiProfile);
    List<LocationPing> findByDidiProfileAndTimestampAfter(DidiProfile didiProfile, LocalDateTime timestamp);
}
