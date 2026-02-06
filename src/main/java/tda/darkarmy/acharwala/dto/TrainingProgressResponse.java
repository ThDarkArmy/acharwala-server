package tda.darkarmy.acharwala.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tda.darkarmy.acharwala.model.TrainingProgress;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgressResponse {
    private Long id;
    private Long didiProfileId;
    private Long trainingContentId;
    private String trainingTitle;
    private TrainingProgress.ProgressStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer progressPercentage;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
