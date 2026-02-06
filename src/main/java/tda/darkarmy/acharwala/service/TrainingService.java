package tda.darkarmy.acharwala.service;

import tda.darkarmy.acharwala.dto.TrainingContentResponse;
import tda.darkarmy.acharwala.dto.TrainingProgressResponse;
import tda.darkarmy.acharwala.model.TrainingContent;

import java.util.List;

public interface TrainingService {
    // Training Content Management (Admin)
    TrainingContentResponse createTrainingContent(TrainingContent trainingContent);
    TrainingContentResponse updateTrainingContent(Long contentId, TrainingContent trainingContent);
    void deleteTrainingContent(Long contentId);
    TrainingContentResponse getTrainingContent(Long contentId);
    List<TrainingContentResponse> getAllTrainingContent();
    List<TrainingContentResponse> getTrainingContentByDifficulty(TrainingContent.Difficulty difficulty);

    // Training Progress Tracking
    TrainingProgressResponse startTraining(Long trainingContentId);
    TrainingProgressResponse updateTrainingProgress(Long trainingContentId, Integer progressPercentage);
    TrainingProgressResponse completeTraining(Long trainingContentId);
    TrainingProgressResponse failTraining(Long trainingContentId);
    List<TrainingProgressResponse> getDidiTrainingProgress();
    Integer calculateOverallTrainingCompletion();
}
