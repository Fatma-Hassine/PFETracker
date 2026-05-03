package com.pfetracker.controller.module2;

import com.pfetracker.dto.module2.CreateSubTaskRequest;
import com.pfetracker.dto.module2.CreateTaskRequest;
import com.pfetracker.dto.module2.UpdateTaskStatusRequest;
import com.pfetracker.entity.module2.SubTask;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.entity.module2.TaskHistory;
import com.pfetracker.service.module2.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller des tâches et sous-tâches du Module 2.
 */
@RestController
@RequestMapping("/api/v2/pfeTrack")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Liste les tâches d'un jalon.
     */
    @GetMapping("/milestones/{milestoneId}/tasks")
    public List<Task> getTasksByMilestone(@PathVariable Long milestoneId) {
        return taskService.getTasksByMilestone(milestoneId);
    }

    /**
     * Liste toutes les tâches d'un PFE.
     */
    @GetMapping("/pfes/{pfeId}/tasks")
    public List<Task> getTasksByPfe(@PathVariable Long pfeId) {
        return taskService.getTasksByPfe(pfeId);
    }

    /**
     * Récupère une tâche précise.
     */
    @GetMapping("/tasks/{taskId}")
    public Task getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId);
    }

    /**
     * Crée une tâche dans un jalon.
     */
    @PostMapping("/milestones/{milestoneId}/tasks")
    public Task createTask(
            @PathVariable Long milestoneId,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        return taskService.createTask(milestoneId, request);
    }

    /**
     * Change le statut d'une tâche.
     */
    @PatchMapping("/tasks/{taskId}/status")
    public Task updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request
    ) {
        return taskService.updateTaskStatus(taskId, request);
    }

    /**
     * Crée une sous-tâche.
     */
    @PostMapping("/tasks/{taskId}/subtasks")
    public SubTask createSubTask(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateSubTaskRequest request
    ) {
        return taskService.createSubTask(taskId, request);
    }

    /**
     * Change l'état d'une sous-tâche : done / not done.
     */
    @PatchMapping("/subtasks/{subTaskId}/toggle")
    public SubTask toggleSubTask(@PathVariable Long subTaskId) {
        return taskService.toggleSubTask(subTaskId);
    }

    /**
     * Historique des changements de statut d'une tâche.
     */
    @GetMapping("/tasks/{taskId}/history")
    public List<TaskHistory> getTaskHistory(@PathVariable Long taskId) {
        return taskService.getTaskHistory(taskId);
    }
}