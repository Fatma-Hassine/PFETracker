package com.pfetracker.controller.module2;

import com.pfetracker.dto.module2.UpdateMilestoneRequest;
import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.service.module2.MilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller des jalons du Module 2.
 */
@RestController
@RequestMapping("/api/module2")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    /**
     * Liste les jalons d'un PFE.
     */
    @GetMapping("/pfes/{pfeId}/milestones")
    public List<Milestone> getMilestonesByPfe(@PathVariable Long pfeId) {
        return milestoneService.getMilestonesByPfe(pfeId);
    }

    /**
     * Récupère un jalon précis.
     */
    @GetMapping("/milestones/{milestoneId}")
    public Milestone getMilestoneById(@PathVariable Long milestoneId) {
        return milestoneService.getMilestoneById(milestoneId);
    }

    /**
     * Met à jour les infos d'un jalon : dates, poids, commentaire encadrant.
     */
    @PutMapping("/milestones/{milestoneId}")
    public Milestone updateMilestone(
            @PathVariable Long milestoneId,
            @RequestBody UpdateMilestoneRequest request
    ) {
        return milestoneService.updateMilestone(milestoneId, request);
    }
}