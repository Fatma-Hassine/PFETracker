package com.pfetracker.service.module2;

import com.pfetracker.entity.module2.AiAlert;
import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.Sprint;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.entity.module2.enums.AlertSeverity;
import com.pfetracker.entity.module2.enums.AlertType;
import com.pfetracker.entity.module2.enums.PfeStatus;
import com.pfetracker.entity.module2.enums.SprintStatus;
import com.pfetracker.entity.module2.enums.TaskStatus;
import com.pfetracker.repository.module2.AiAlertRepository;
import com.pfetracker.repository.module2.MilestoneRepository;
import com.pfetracker.repository.module2.PfeRepository;
import com.pfetracker.repository.module2.SprintRepository;
import com.pfetracker.repository.module2.Module2TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Moteur IA simple basé sur des règles.
 *
 * Il détecte :
 * - tâches en retard
 * - jalons en retard
 * - PFE à risque
 * - stagnation
 * - sprint non clôturé
 */
@Service
@RequiredArgsConstructor
public class AiDelayDetectionService {

    private final PfeRepository pfeRepository;
    private final MilestoneRepository milestoneRepository;
    private final Module2TaskRepository taskRepository;
    private final SprintRepository sprintRepository;
    private final AiAlertRepository aiAlertRepository;

    // MODIF : seuil de stagnation configurable (cahier des charges §5.7.1 : 7 jours par défaut)
    @Value("${pfe.stagnation.seuil-jours:7}")
    private int seuilStagnationJours;

    /**
     * Exécution chaque nuit à 2h00 (cahier des charges §5.7 : "s'exécute
     * automatiquement chaque nuit à 2h00 du matin").
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void runDetection() {
        detectLateTasks();
        detectLateMilestones();
        detectPfeRisks();
        detectSprintProblems();
    }

    public void runDetectionManually() {
        runDetection();
    }

    private void detectLateTasks() {
        List<Task> lateTasks = taskRepository.findByDeadlineBeforeAndStatusNot(
                LocalDate.now(),
                TaskStatus.VALIDATED
        );

        for (Task task : lateTasks) {
            Long pfeId = task.getMilestone().getPfe().getId();

            createAlertIfNotExists(
                    pfeId,
                    task.getMilestone().getPfe().getStudentId(),
                    task.getMilestone().getPfe().getSupervisorId(),
                    AlertType.TASK_LATE,
                    AlertSeverity.WARNING,
                    "La tâche '" + task.getTitle() + "' est en retard.",
                    "TASK",
                    task.getId()
            );
        }
    }

    private void detectLateMilestones() {
        List<Pfe> pfes = pfeRepository.findAll();

        for (Pfe pfe : pfes) {
            List<Milestone> milestones = milestoneRepository.findByPfeIdOrderByOrderIndexAsc(pfe.getId());

            for (Milestone milestone : milestones) {
                boolean isLate = milestone.getPlannedEndDate() != null
                        && milestone.getPlannedEndDate().isBefore(LocalDate.now())
                        && milestone.getProgress() != null
                        && milestone.getProgress() < 100.0;

                if (isLate) {
                    pfe.setStatus(PfeStatus.LATE);
                    pfeRepository.save(pfe);

                    createAlertIfNotExists(
                            pfe.getId(),
                            pfe.getStudentId(),
                            pfe.getSupervisorId(),
                            AlertType.MILESTONE_LATE,
                            AlertSeverity.CRITICAL,
                            "Le jalon '" + milestone.getTitle() + "' est en retard.",
                            "MILESTONE",
                            milestone.getId()
                    );
                }
            }
        }
    }

    private void detectPfeRisks() {
        List<Pfe> pfes = pfeRepository.findAll();

        for (Pfe pfe : pfes) {
            if (pfe.getStartDate() == null || pfe.getDefenseDate() == null) {
                continue;
            }

            long totalDays = ChronoUnit.DAYS.between(pfe.getStartDate(), pfe.getDefenseDate());
            long elapsedDays = ChronoUnit.DAYS.between(pfe.getStartDate(), LocalDate.now());

            if (totalDays <= 0) {
                continue;
            }

            double expectedProgress = Math.min(100.0, (elapsedDays * 100.0) / totalDays);
            double realProgress = pfe.getProgress() != null ? pfe.getProgress() : 0.0;

            if (expectedProgress - realProgress >= 25.0) {
                createAlertIfNotExists(
                        pfe.getId(),
                        pfe.getStudentId(),
                        pfe.getSupervisorId(),
                        AlertType.PFE_RISK,
                        AlertSeverity.CRITICAL,
                        "Le PFE semble à risque : progression réelle "
                                + Math.round(realProgress)
                                + "% contre progression attendue "
                                + Math.round(expectedProgress)
                                + "%.",
                        "PFE",
                        pfe.getId()
                );
            }

            List<Task> recentTasks = taskRepository.findByMilestonePfeIdAndUpdatedAtAfter(
                    pfe.getId(),
                    LocalDateTime.now().minusDays(seuilStagnationJours)
            );

            if (recentTasks.isEmpty() && realProgress < 100.0) {
                createAlertIfNotExists(
                        pfe.getId(),
                        pfe.getStudentId(),
                        pfe.getSupervisorId(),
                        AlertType.STUDENT_STAGNATION,
                        AlertSeverity.WARNING,
                        "Aucune activité récente détectée sur ce PFE depuis " + seuilStagnationJours + " jours.",
                        "PFE",
                        pfe.getId()
                );
            }
        }
    }

    private void detectSprintProblems() {
        List<Sprint> lateSprints = sprintRepository.findByEndDateBeforeAndStatusNot(
                LocalDate.now(),
                SprintStatus.CLOSED
        );

        for (Sprint sprint : lateSprints) {
            createAlertIfNotExists(
                    sprint.getPfe().getId(),
                    sprint.getPfe().getStudentId(),
                    sprint.getPfe().getSupervisorId(),
                    AlertType.SPRINT_NOT_CLOSED,
                    AlertSeverity.WARNING,
                    "Le sprint '" + sprint.getObjective() + "' est terminé mais pas clôturé.",
                    "SPRINT",
                    sprint.getId()
            );
        }

        List<Sprint> submittedSprints = sprintRepository.findByStatusAndUpdatedAtBefore(
                SprintStatus.REVIEW_SUBMITTED,
                LocalDateTime.now().minusDays(7)
        );

        for (Sprint sprint : submittedSprints) {
            createAlertIfNotExists(
                    sprint.getPfe().getId(),
                    sprint.getPfe().getStudentId(),
                    sprint.getPfe().getSupervisorId(),
                    AlertType.SPRINT_REVIEW_NOT_VALIDATED,
                    AlertSeverity.WARNING,
                    "Le bilan du sprint '" + sprint.getObjective() + "' attend une validation depuis plus de 7 jours.",
                    "SPRINT",
                    sprint.getId()
            );
        }
    }

    private void createAlertIfNotExists(
            Long pfeId,
            Long studentId,
            Long supervisorId,
            AlertType type,
            AlertSeverity severity,
            String message,
            String referenceType,
            Long referenceId
    ) {
        boolean exists = aiAlertRepository.existsByPfeIdAndTypeAndReferenceTypeAndReferenceIdAndResolvedFalse(
                pfeId,
                type,
                referenceType,
                referenceId
        );

        if (exists) {
            return;
        }

        AiAlert alert = new AiAlert();
        alert.setPfeId(pfeId);
        alert.setStudentId(studentId);
        alert.setSupervisorId(supervisorId);
        alert.setType(type);
        alert.setSeverity(severity);
        alert.setMessage(message);
        alert.setReferenceType(referenceType);
        alert.setReferenceId(referenceId);
        alert.setResolved(false);

        aiAlertRepository.save(alert);
    }
}