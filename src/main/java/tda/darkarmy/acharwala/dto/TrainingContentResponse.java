package tda.darkarmy.acharwala.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tda.darkarmy.acharwala.model.TrainingContent;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingContentResponse {
    private Long id;
    private String title;
    private String description;
    private TrainingContent.ContentType contentType;
    private String contentUrl;
    private String thumbnailUrl;
    private String content;
    private Integer sequenceOrder;
    private TrainingContent.Difficulty difficulty;
    private Long durationInMinutes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
