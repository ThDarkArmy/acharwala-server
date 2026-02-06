package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tda.darkarmy.acharwala.model.TrainingProgress;
import tda.darkarmy.acharwala.model.DidiProfile;
import tda.darkarmy.acharwala.model.TrainingContent;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingProgressRepository extends JpaRepository<TrainingProgress, Long> {
    List<TrainingProgress> findByDidiProfileOrderByCreatedAtDesc(DidiProfile didiProfile);
    Optional<TrainingProgress> findByDidiProfileAndTrainingContent(DidiProfile didiProfile, TrainingContent content);
    List<TrainingProgress> findByDidiProfileAndStatus(DidiProfile didiProfile, TrainingProgress.ProgressStatus status);
}
