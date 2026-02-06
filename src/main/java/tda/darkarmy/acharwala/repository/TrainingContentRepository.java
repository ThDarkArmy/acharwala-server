package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tda.darkarmy.acharwala.model.TrainingContent;

import java.util.List;

@Repository
public interface TrainingContentRepository extends JpaRepository<TrainingContent, Long> {
    List<TrainingContent> findByIsActiveTrueOrderBySequenceOrder();
    List<TrainingContent> findByDifficultyOrderBySequenceOrder(TrainingContent.Difficulty difficulty);
}
