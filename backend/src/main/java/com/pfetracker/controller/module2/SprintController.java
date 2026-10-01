package com.pfetracker.controller.module2;

import com.pfetracker.dto.module2.CreateSprintRequest;
import com.pfetracker.dto.module2.SubmitSprintReviewRequest;
import com.pfetracker.dto.module2.ValidateSprintRequest;
import com.pfetracker.entity.module2.Sprint;
import com.pfetracker.service.module2.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller des mini-sprints.
 */
@RestController
@RequestMapping("/v2/pfeTrack")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    /**
     * Liste les sprints d'un PFE.
     */
    @GetMapping("/pfes/{pfeId}/sprints")
    public List<Sprint> getSprintsByPfe(@PathVariable Long pfeId) {
        return sprintService.getSprintsByPfe(pfeId);
    }

    /**
     * Récupère un sprint précis.
     */
    @GetMapping("/sprints/{sprintId}")
    public Sprint getSprintById(@PathVariable Long sprintId) {
        return sprintService.getSprintById(sprintId);
    }

    /**
     * Crée un sprint.
     */
    @PostMapping("/pfes/{pfeId}/sprints")
    public Sprint createSprint(
            @PathVariable Long pfeId,
            @Valid @RequestBody CreateSprintRequest request
    ) {
        return sprintService.createSprint(pfeId, request);
    }

    /**
     * L'étudiant soumet son bilan de sprint.
     */
    @PostMapping("/sprints/{sprintId}/review")
    public Sprint submitSprintReview(
            @PathVariable Long sprintId,
            @Valid @RequestBody SubmitSprintReviewRequest request
    ) {
        return sprintService.submitSprintReview(sprintId, request);
    }

    /**
     * L'encadrant valide ou refuse le bilan.
     */
    @PostMapping("/sprints/{sprintId}/validate")
    public Sprint validateSprint(
            @PathVariable Long sprintId,
            @RequestBody ValidateSprintRequest request
    ) {
        return sprintService.validateSprint(sprintId, request);
    }

    /**
     * Clôture un sprint.
     */
    @PostMapping("/sprints/{sprintId}/close")
    public Sprint closeSprint(@PathVariable Long sprintId) {
        return sprintService.closeSprint(sprintId);
    }
}