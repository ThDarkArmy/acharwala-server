package tda.darkarmy.acharwala.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tda.darkarmy.acharwala.dto.TrainingContentResponse;
import tda.darkarmy.acharwala.dto.TrainingProgressResponse;
import tda.darkarmy.acharwala.model.TrainingContent;
import tda.darkarmy.acharwala.service.TrainingService;

import java.util.List;

@RestController
@RequestMapping("/api/didi/training")
@RequiredArgsConstructor
@Tag(name = "Didi Training Management", description = "APIs for training content delivery and progress tracking")
public class DidiTrainingController {

    private final TrainingService trainingService;

    // Training Content Management (Admin)
    @Operation(summary = "Create training content", description = "Admin creates new training content")
    @PostMapping("/content")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainingContentResponse> createTrainingContent(
            @Valid @RequestBody TrainingContent trainingContent) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingService.createTrainingContent(trainingContent));
    }

    @Operation(summary = "Update training content", description = "Admin updates training content")
    @PutMapping("/content/{contentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainingContentResponse> updateTrainingContent(
            @PathVariable Long contentId,
            @Valid @RequestBody TrainingContent trainingContent) {
        return ResponseEntity.ok(trainingService.updateTrainingContent(contentId, trainingContent));
    }

    @Operation(summary = "Delete training content", description = "Admin deletes training content")
    @DeleteMapping("/content/{contentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTrainingContent(@PathVariable Long contentId) {
        trainingService.deleteTrainingContent(contentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get training content", description = "Get specific training content")
    @GetMapping("/content/{contentId}")
    public ResponseEntity<TrainingContentResponse> getTrainingContent(@PathVariable Long contentId) {
        return ResponseEntity.ok(trainingService.getTrainingContent(contentId));
    }

    @Operation(summary = "Get all training content", description = "Get all active training content")
    @GetMapping("/content")
    public ResponseEntity<List<TrainingContentResponse>> getAllTrainingContent() {
        return ResponseEntity.ok(trainingService.getAllTrainingContent());
    }

    @Operation(summary = "Get training by difficulty", description = "Get training content by difficulty level")
    @GetMapping("/content/difficulty/{difficulty}")
    public ResponseEntity<List<TrainingContentResponse>> getTrainingContentByDifficulty(
            @PathVariable TrainingContent.Difficulty difficulty) {
        return ResponseEntity.ok(trainingService.getTrainingContentByDifficulty(difficulty));
    }

    // Training Progress Tracking (Didi)
    @Operation(summary = "Start training", description = "Didi starts a training module")
    @PostMapping("/start/{trainingContentId}")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<TrainingProgressResponse> startTraining(@PathVariable Long trainingContentId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingService.startTraining(trainingContentId));
    }

    @Operation(summary = "Update progress", description = "Update training progress percentage")
    @PutMapping("/progress/{trainingContentId}")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<TrainingProgressResponse> updateTrainingProgress(
            @PathVariable Long trainingContentId,
            @RequestParam Integer progressPercentage) {
        return ResponseEntity.ok(trainingService.updateTrainingProgress(trainingContentId, progressPercentage));
    }

    @Operation(summary = "Complete training", description = "Mark training as completed")
    @PutMapping("/complete/{trainingContentId}")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<TrainingProgressResponse> completeTraining(@PathVariable Long trainingContentId) {
        return ResponseEntity.ok(trainingService.completeTraining(trainingContentId));
    }

    @Operation(summary = "Fail training", description = "Mark training as failed")
    @PutMapping("/fail/{trainingContentId}")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<TrainingProgressResponse> failTraining(@PathVariable Long trainingContentId) {
        return ResponseEntity.ok(trainingService.failTraining(trainingContentId));
    }

    @Operation(summary = "Get my training progress", description = "Get Didi's training progress")
    @GetMapping("/my-progress")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<List<TrainingProgressResponse>> getDidiTrainingProgress() {
        return ResponseEntity.ok(trainingService.getDidiTrainingProgress());
    }

    @Operation(summary = "Get overall completion", description = "Get overall training completion percentage")
    @GetMapping("/completion-percentage")
    @PreAuthorize("hasRole('SHG_DIDI')")
    public ResponseEntity<Integer> getOverallTrainingCompletion() {
        return ResponseEntity.ok(trainingService.calculateOverallTrainingCompletion());
    }
}
