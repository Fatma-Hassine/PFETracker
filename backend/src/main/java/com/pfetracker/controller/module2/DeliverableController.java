package com.pfetracker.controller.module2;

import com.pfetracker.entity.module2.Deliverable;
import com.pfetracker.service.module2.DeliverableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller des livrables du Module 2.
 */
@RestController
@RequestMapping("/v2/pfeTrack")
@RequiredArgsConstructor
public class DeliverableController {

    private final DeliverableService deliverableService;

    /**
     * Liste les livrables d'une tâche.
     */
    @GetMapping("/tasks/{taskId}/deliverables")
    public List<Deliverable> getDeliverablesByTask(@PathVariable Long taskId) {
        return deliverableService.getDeliverablesByTask(taskId);
    }

    /**
     * Upload d'un livrable lié à une tâche.
     */
    @PostMapping("/tasks/{taskId}/deliverables")
    public Deliverable uploadTaskDeliverable(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file
    ) {
        return deliverableService.uploadTaskDeliverable(taskId, file);
    }

    /**
     * Liste les livrables d'un jalon.
     */
    @GetMapping("/milestones/{milestoneId}/deliverables")
    public List<Deliverable> getDeliverablesByMilestone(@PathVariable Long milestoneId) {
        return deliverableService.getDeliverablesByMilestone(milestoneId);
    }

    /**
     * Upload d'un livrable lié à un jalon.
     */
    @PostMapping("/milestones/{milestoneId}/deliverables")
    public Deliverable uploadMilestoneDeliverable(
            @PathVariable Long milestoneId,
            @RequestParam("file") MultipartFile file
    ) {
        return deliverableService.uploadMilestoneDeliverable(milestoneId, file);
    }
}