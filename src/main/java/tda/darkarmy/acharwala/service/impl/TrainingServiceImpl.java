package tda.darkarmy.acharwala.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tda.darkarmy.acharwala.dto.TrainingContentResponse;
import tda.darkarmy.acharwala.dto.TrainingProgressResponse;
import tda.darkarmy.acharwala.exception.ResourceNotFoundException;
import tda.darkarmy.acharwala.model.DidiProfile;
import tda.darkarmy.acharwala.model.TrainingContent;
import tda.darkarmy.acharwala.model.TrainingProgress;
import tda.darkarmy.acharwala.model.User;
import tda.darkarmy.acharwala.repository.DidiProfileRepository;
import tda.darkarmy.acharwala.repository.TrainingContentRepository;
import tda.darkarmy.acharwala.repository.TrainingProgressRepository;
import tda.darkarmy.acharwala.service.TrainingService;
import tda.darkarmy.acharwala.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingContentRepository trainingContentRepository;
    private final TrainingProgressRepository trainingProgressRepository;
    private final DidiProfileRepository didiProfileRepository;
    private final UserService userService;

    @Override
    public TrainingContentResponse createTrainingContent(TrainingContent trainingContent) {
        TrainingContent saved = trainingContentRepository.save(trainingContent);
        log.info("Training content created with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    public TrainingContentResponse updateTrainingContent(Long contentId, TrainingContent updatedContent) {
        TrainingContent content = trainingContentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Training content not found"));

        if (updatedContent.getTitle() != null) content.setTitle(updatedContent.getTitle());
        if (updatedContent.getDescription() != null) content.setDescription(updatedContent.getDescription());
        if (updatedContent.getContentType() != null) content.setContentType(updatedContent.getContentType());
        if (updatedContent.getContentUrl() != null) content.setContentUrl(updatedContent.getContentUrl());
        if (updatedContent.getThumbnailUrl() != null) content.setThumbnailUrl(updatedContent.getThumbnailUrl());
        if (updatedContent.getContent() != null) content.setContent(updatedContent.getContent());
        if (updatedContent.getSequenceOrder() != null) content.setSequenceOrder(updatedContent.getSequenceOrder());
        if (updatedContent.getDifficulty() != null) content.setDifficulty(updatedContent.getDifficulty());
        if (updatedContent.getDurationInMinutes() != null) content.setDurationInMinutes(updatedContent.getDurationInMinutes());
        if (updatedContent.getIsActive() != null) content.setIsActive(updatedContent.getIsActive());

        TrainingContent saved = trainingContentRepository.save(content);
        return mapToResponse(saved);
    }

    @Override
    public void deleteTrainingContent(Long contentId) {
        TrainingContent content = trainingContentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Training content not found"));
        trainingContentRepository.delete(content);
        log.info("Training content deleted with ID: {}", contentId);
    }

    @Override
    public TrainingContentResponse getTrainingContent(Long contentId) {
        TrainingContent content = trainingContentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Training content not found"));
        return mapToResponse(content);
    }

    @Override
    public List<TrainingContentResponse> getAllTrainingContent() {
        List<TrainingContent> contents = trainingContentRepository.findByIsActiveTrueOrderBySequenceOrder();
        return contents.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<TrainingContentResponse> getTrainingContentByDifficulty(TrainingContent.Difficulty difficulty) {
        List<TrainingContent> contents = trainingContentRepository.findByDifficultyOrderBySequenceOrder(difficulty);
        return contents.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public TrainingProgressResponse startTraining(Long trainingContentId) {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        TrainingContent content = trainingContentRepository.findById(trainingContentId)
                .orElseThrow(() -> new ResourceNotFoundException("Training content not found"));

        TrainingProgress existingProgress = trainingProgressRepository
                .findByDidiProfileAndTrainingContent(didiProfile, content).orElse(null);

        if (existingProgress != null && existingProgress.getStatus() == TrainingProgress.ProgressStatus.COMPLETED) {
            throw new RuntimeException("Training already completed");
        }

        TrainingProgress progress = TrainingProgress.builder()
                .didiProfile(didiProfile)
                .trainingContent(content)
                .status(TrainingProgress.ProgressStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .progressPercentage(0)
                .build();

        TrainingProgress saved = trainingProgressRepository.save(progress);
        log.info("Training started for Didi ID: {} - Content ID: {}", didiProfile.getId(), trainingContentId);
        return mapToResponse(saved);
    }

    @Override
    public TrainingProgressResponse updateTrainingProgress(Long trainingContentId, Integer progressPercentage) {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        TrainingContent content = trainingContentRepository.findById(trainingContentId)
                .orElseThrow(() -> new ResourceNotFoundException("Training content not found"));

        TrainingProgress progress = trainingProgressRepository.findByDidiProfileAndTrainingContent(didiProfile, content)
                .orElseThrow(() -> new RuntimeException("Training not started"));

        if (progressPercentage < 0 || progressPercentage > 100) {
            throw new IllegalArgumentException("Progress percentage must be between 0 and 100");
        }

        progress.setProgressPercentage(progressPercentage);
        TrainingProgress saved = trainingProgressRepository.save(progress);
        return mapToResponse(saved);
    }

    @Override
    public TrainingProgressResponse completeTraining(Long trainingContentId) {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        TrainingContent content = trainingContentRepository.findById(trainingContentId)
                .orElseThrow(() -> new ResourceNotFoundException("Training content not found"));

        TrainingProgress progress = trainingProgressRepository.findByDidiProfileAndTrainingContent(didiProfile, content)
                .orElseThrow(() -> new RuntimeException("Training not started"));

        progress.setStatus(TrainingProgress.ProgressStatus.COMPLETED);
        progress.setCompletedAt(LocalDateTime.now());
        progress.setProgressPercentage(100);
        TrainingProgress saved = trainingProgressRepository.save(progress);

        updateDidiTrainingStatus(didiProfile);
        log.info("Training completed for Didi ID: {} - Content ID: {}", didiProfile.getId(), trainingContentId);
        return mapToResponse(saved);
    }

    @Override
    public TrainingProgressResponse failTraining(Long trainingContentId) {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        TrainingContent content = trainingContentRepository.findById(trainingContentId)
                .orElseThrow(() -> new ResourceNotFoundException("Training content not found"));

        TrainingProgress progress = trainingProgressRepository.findByDidiProfileAndTrainingContent(didiProfile, content)
                .orElseThrow(() -> new RuntimeException("Training not started"));

        progress.setStatus(TrainingProgress.ProgressStatus.FAILED);
        TrainingProgress saved = trainingProgressRepository.save(progress);
        log.info("Training failed for Didi ID: {} - Content ID: {}", didiProfile.getId(), trainingContentId);
        return mapToResponse(saved);
    }

    @Override
    public List<TrainingProgressResponse> getDidiTrainingProgress() {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        List<TrainingProgress> progressList = trainingProgressRepository.findByDidiProfileOrderByCreatedAtDesc(didiProfile);
        return progressList.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public Integer calculateOverallTrainingCompletion() {
        User user = userService.getLoggedInUser();
        DidiProfile didiProfile = didiProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Didi profile not found"));

        List<TrainingProgress> progressList = trainingProgressRepository.findByDidiProfileOrderByCreatedAtDesc(didiProfile);
        if (progressList.isEmpty()) return 0;

        long completedCount = progressList.stream()
                .filter(p -> p.getStatus() == TrainingProgress.ProgressStatus.COMPLETED).count();

        return (int) ((completedCount * 100) / progressList.size());
    }

    private void updateDidiTrainingStatus(DidiProfile didiProfile) {
        List<TrainingProgress> allProgress = trainingProgressRepository.findByDidiProfileOrderByCreatedAtDesc(didiProfile);
        long completedCount = allProgress.stream()
                .filter(p -> p.getStatus() == TrainingProgress.ProgressStatus.COMPLETED).count();

        if (completedCount == allProgress.size() && !allProgress.isEmpty()) {
            didiProfile.setTrainingStatus(DidiProfile.TrainingStatus.COMPLETED);
            didiProfile.setTrainingCompletedAt(LocalDateTime.now());
            didiProfileRepository.save(didiProfile);
            log.info("Didi ID: {} completed all training", didiProfile.getId());
        }
    }

    private TrainingContentResponse mapToResponse(TrainingContent content) {
        return TrainingContentResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .contentType(content.getContentType())
                .contentUrl(content.getContentUrl())
                .thumbnailUrl(content.getThumbnailUrl())
                .content(content.getContent())
                .sequenceOrder(content.getSequenceOrder())
                .difficulty(content.getDifficulty())
                .durationInMinutes(content.getDurationInMinutes())
                .isActive(content.getIsActive())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .build();
    }

    private TrainingProgressResponse mapToResponse(TrainingProgress progress) {
        return TrainingProgressResponse.builder()
                .id(progress.getId())
                .didiProfileId(progress.getDidiProfile().getId())
                .trainingContentId(progress.getTrainingContent().getId())
                .trainingTitle(progress.getTrainingContent().getTitle())
                .status(progress.getStatus())
                .startedAt(progress.getStartedAt())
                .completedAt(progress.getCompletedAt())
                .progressPercentage(progress.getProgressPercentage())
                .notes(progress.getNotes())
                .createdAt(progress.getCreatedAt())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }
}
