package com.pfetracker.service.module2;

import com.pfetracker.entity.module2.Deliverable;
import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.repository.module2.DeliverableRepository;
import com.pfetracker.repository.module2.MilestoneRepository;
import com.pfetracker.repository.module2.Module2TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliverableService {

    private final DeliverableRepository deliverableRepository;
    private final Module2TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private final CurrentUserService currentUserService;

    private final Path uploadRoot = Path.of("uploads", "module2");

    public List<Deliverable> getDeliverablesByTask(Long taskId) {
        return deliverableRepository.findByTaskIdOrderByVersionNumberDesc(taskId);
    }

    public List<Deliverable> getDeliverablesByMilestone(Long milestoneId) {
        return deliverableRepository.findByMilestoneIdOrderByVersionNumberDesc(milestoneId);
    }

    @Transactional
    public Deliverable uploadTaskDeliverable(Long taskId, MultipartFile file) {
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Tâche introuvable avec id = " + taskId));

            Files.createDirectories(uploadRoot);

            int nextVersion = deliverableRepository.countByTaskId(taskId) + 1;

            String originalFileName = file.getOriginalFilename();
            String extension = extractExtension(originalFileName);
            String storedFileName = "task-" + taskId + "-v" + nextVersion + "-" + UUID.randomUUID() + extension;

            Path targetPath = uploadRoot.resolve(storedFileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Deliverable deliverable = new Deliverable();
            deliverable.setTask(task);
            deliverable.setOriginalFileName(originalFileName);
            deliverable.setStoredFileName(storedFileName);
            deliverable.setContentType(file.getContentType());
            deliverable.setSizeBytes(file.getSize());
            deliverable.setVersionNumber(nextVersion);
            deliverable.setFilePath(targetPath.toString());
            deliverable.setUploadedByUserId(currentUserService.getCurrentUserId());

            return deliverableRepository.save(deliverable);
        } catch (Exception exception) {
            throw new RuntimeException("Erreur lors de l'upload du livrable", exception);
        }
    }

    @Transactional
    public Deliverable uploadMilestoneDeliverable(Long milestoneId, MultipartFile file) {
        try {
            Milestone milestone = milestoneRepository.findById(milestoneId)
                    .orElseThrow(() -> new RuntimeException("Jalon introuvable avec id = " + milestoneId));

            Files.createDirectories(uploadRoot);

            int nextVersion = deliverableRepository.countByMilestoneId(milestoneId) + 1;

            String originalFileName = file.getOriginalFilename();
            String extension = extractExtension(originalFileName);
            String storedFileName = "milestone-" + milestoneId + "-v" + nextVersion + "-" + UUID.randomUUID() + extension;

            Path targetPath = uploadRoot.resolve(storedFileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Deliverable deliverable = new Deliverable();
            deliverable.setMilestone(milestone);
            deliverable.setOriginalFileName(originalFileName);
            deliverable.setStoredFileName(storedFileName);
            deliverable.setContentType(file.getContentType());
            deliverable.setSizeBytes(file.getSize());
            deliverable.setVersionNumber(nextVersion);
            deliverable.setFilePath(targetPath.toString());
            deliverable.setUploadedByUserId(currentUserService.getCurrentUserId());

            return deliverableRepository.save(deliverable);
        } catch (Exception exception) {
            throw new RuntimeException("Erreur lors de l'upload du livrable du jalon", exception);
        }
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf("."));
    }
}