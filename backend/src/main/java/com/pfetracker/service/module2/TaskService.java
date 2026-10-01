package com.pfetracker.service.module2;

import com.pfetracker.dto.module2.CreateSubTaskRequest;
import com.pfetracker.dto.module2.CreateTaskRequest;
import com.pfetracker.dto.module2.UpdateTaskStatusRequest;
import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.entity.module2.SubTask;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.entity.module2.TaskHistory;
import com.pfetracker.entity.module2.enums.TaskStatus;
import com.pfetracker.repository.module2.MilestoneRepository;
import com.pfetracker.repository.module2.Module2TaskRepository;
import com.pfetracker.repository.module2.SubTaskRepository;
import com.pfetracker.repository.module2.TaskHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final Module2TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private final SubTaskRepository subTaskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final ProgressService progressService;
    private final CurrentUserService currentUserService;
    private final com.pfetracker.service.module3.NotificationService notificationService;

    /** Soumission -> encadrant ; validation/correction/assignation -> étudiant (l'autre partie que l'auteur). */
    private void notifierSansBloquer(Task task, String action) {
        try {
            com.pfetracker.entity.module2.Pfe pfe = task.getMilestone().getPfe();
            Long auteur = currentUserService.getCurrentUserId();
            Long destinataire = auteur.equals(pfe.getStudentId()) ? pfe.getSupervisorId() : pfe.getStudentId();
            if (destinataire != null && !destinataire.equals(auteur)) {
                notificationService.createTaskNotification(destinataire, task, action);
            }
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(TaskService.class).warn("Notification tâche non envoyée : {}", e.getMessage());
        }
    }

    public List<Task> getTasksByMilestone(Long milestoneId) {
        return taskRepository.findByMilestoneId(milestoneId);
    }

    public List<Task> getTasksByPfe(Long pfeId) {
        return taskRepository.findByMilestonePfeId(pfeId);
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tâche introuvable avec id = " + taskId));
    }

    @Transactional
    public Task createTask(Long milestoneId, CreateTaskRequest request) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Jalon introuvable avec id = " + milestoneId));

        Task task = new Task();
        task.setMilestone(milestone);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());
        task.setPriority(request.getPriority());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setAssignedStudentId(request.getAssignedStudentId());
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setProgress(0.0);
        task.setCreatedByUserId(currentUserService.getCurrentUserId());
        task.setCreatedByRole(currentUserService.getCurrentUserRole());

        Task saved = taskRepository.save(task);
        progressService.recalculateAfterTaskChange(saved);
        notifierSansBloquer(saved, "ASSIGNED");
        return saved;
    }

    @Transactional
    public Task updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        Task task = getTaskById(taskId);

        TaskStatus oldStatus = task.getStatus();
        TaskStatus newStatus = request.getStatus();

        task.setStatus(newStatus);
        task.setCorrectionNote(request.getComment());

        Task saved = taskRepository.save(task);

        TaskHistory history = new TaskHistory();
        history.setTask(saved);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedByUserId(currentUserService.getCurrentUserId());
        history.setChangedByRole(currentUserService.getCurrentUserRole());
        history.setComment(request.getComment());

        taskHistoryRepository.save(history);

        progressService.recalculateAfterTaskChange(saved);

        if (newStatus == TaskStatus.SUBMITTED || newStatus == TaskStatus.VALIDATED
                || newStatus == TaskStatus.TO_CORRECT) {
            notifierSansBloquer(saved, newStatus.name());
        }

        return saved;
    }

    @Transactional
    public SubTask createSubTask(Long taskId, CreateSubTaskRequest request) {
        Task task = getTaskById(taskId);

        SubTask subTask = new SubTask();
        subTask.setTask(task);
        subTask.setTitle(request.getTitle());
        subTask.setDone(false);

        return subTaskRepository.save(subTask);
    }

    @Transactional
    public SubTask toggleSubTask(Long subTaskId) {
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new RuntimeException("Sous-tâche introuvable avec id = " + subTaskId));

        subTask.setDone(!Boolean.TRUE.equals(subTask.getDone()));

        return subTaskRepository.save(subTask);
    }

    public List<TaskHistory> getTaskHistory(Long taskId) {
        return taskHistoryRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }
}