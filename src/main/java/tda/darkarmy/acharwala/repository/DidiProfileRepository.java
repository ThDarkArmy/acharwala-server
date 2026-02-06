package tda.darkarmy.acharwala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tda.darkarmy.acharwala.model.DidiProfile;
import tda.darkarmy.acharwala.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface DidiProfileRepository extends JpaRepository<DidiProfile, Long> {
    Optional<DidiProfile> findByUser(User user);
    Optional<DidiProfile> findByAadhaarNumber(String aadhaarNumber);
    List<DidiProfile> findByApprovalStatus(DidiProfile.ApprovalStatus status);
    List<DidiProfile> findByTrainingStatus(DidiProfile.TrainingStatus status);
}
