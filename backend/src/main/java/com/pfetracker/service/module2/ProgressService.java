package com.pfetracker.service.module2;

import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.ProgressSnapshot;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.entity.module2.enums.MilestoneStatus;
import com.pfetracker.entity.module2.enums.PfeStatus;
import com.pfetracker.entity.module2.enums.TaskStatus;
import com.pfetracker.repository.module2.MilestoneRepository;
import com.pfetracker.repository.module2.PfeRepository;
import com.pfetracker.repository.module2.ProgressSnapshotRepository;
import com.pfetracker.repository.module2.Module2TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Calcule automatiquement :
 * tâche -> jalon -> PFE
 */
@Service
@RequiredArgsConstructor
public class ProgressService {

    private final Module2TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private final PfeRepository pfeRepository;
    private final ProgressSnapshotRepository progressSnapshotRepository;

    @Transactional
    public void recalculateAfterTaskChange(Task task) {
        Milestone milestone = task.getMilestone();

        recalculateMilestoneProgress(milestone.getId());
        recalculatePfeProgress(milestone.getPfe().getId());
    }

    @Transactional
    public void recalculateMilestoneProgress(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Jalon introuvable avec id = " + milestoneId));

        List<Task> tasks = taskRepository.findByMilestoneId(milestoneId);

        if (tasks.isEmpty()) {
            milestone.setProgress(0.0);
            milestone.setStatus(MilestoneStatus.NOT_STARTED);
            milestoneRepository.save(milestone);
            return;
        }

        double total = tasks.stream()
                .mapToDouble(task -> taskStatusToProgress(task.getStatus()))
                .average()
                .orElse(0.0);

        milestone.setProgress(total);

        if (total >= 100.0) {
            milestone.setStatus(MilestoneStatus.COMPLETED);
        } else if (total > 0.0) {
            milestone.setStatus(MilestoneStatus.IN_PROGRESS);
        } else {
            milestone.setStatus(MilestoneStatus.NOT_STARTED);
        }

        milestoneRepository.save(milestone);
    }

    @Transactional
    public void recalculatePfeProgress(Long pfeId) {
        Pfe pfe = pfeRepository.findById(pfeId)
                .orElseThrow(() -> new RuntimeException("PFE introuvable avec id = " + pfeId));

        List<Milestone> milestones = milestoneRepository.findByPfeIdOrderByOrderIndexAsc(pfeId);

        if (milestones.isEmpty()) {
            pfe.setProgress(0.0);
            pfeRepository.save(pfe);
            return;
        }

        double totalWeight = milestones.stream()
                .mapToDouble(m -> m.getWeight() != null ? m.getWeight() : 1.0)
                .sum();

        double weightedProgress = milestones.stream()
                .mapToDouble(m -> {
                    double weight = m.getWeight() != null ? m.getWeight() : 1.0;
                    double progress = m.getProgress() != null ? m.getProgress() : 0.0;
                    return progress * weight;
                })
                .sum();

        double finalProgress = totalWeight == 0.0 ? 0.0 : weightedProgress / totalWeight;

        pfe.setProgress(finalProgress);

        if (finalProgress >= 100.0) {
            pfe.setStatus(PfeStatus.FINISHED);
        } else if (pfe.getStatus() == PfeStatus.INITIALIZED) {
            pfe.setStatus(PfeStatus.IN_PROGRESS);
        }

        pfeRepository.save(pfe);

        ProgressSnapshot snapshot = new ProgressSnapshot();
        snapshot.setPfeId(pfeId);
        snapshot.setProgress(finalProgress);
        progressSnapshotRepository.save(snapshot);
    }

    private double taskStatusToProgress(TaskStatus status) {
        if (status == null) {
            return 0.0;
        }

        return switch (status) {
            case NOT_STARTED -> 0.0;
            case IN_PROGRESS -> 40.0;
            case SUBMITTED -> 80.0;
            case VALIDATED -> 100.0;
            case TO_CORRECT -> 60.0;
            case CANCELLED -> 0.0;
        };
    }
}