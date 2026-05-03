package com.pfetracker.controller.module2;

import com.pfetracker.dto.module2.CreateMilestoneRequest;
import com.pfetracker.dto.module2.CreateMilestonesBulkRequest;
import com.pfetracker.dto.module2.UpdateMilestoneRequest;
import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.service.module2.MilestoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/pfeTrack")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    @GetMapping("/pfes/{pfeId}/milestones")
    public List<Milestone> getMilestonesByPfe(@PathVariable Long pfeId) {
        return milestoneService.getMilestonesByPfe(pfeId);
    }

    @GetMapping("/milestones/{milestoneId}")
    public Milestone getMilestoneById(@PathVariable Long milestoneId) {
        return milestoneService.getMilestoneById(milestoneId);
    }

    @PostMapping("/pfes/{pfeId}/milestones")
    public Milestone createMilestone(
            @PathVariable Long pfeId,
            @Valid @RequestBody CreateMilestoneRequest request
    ) {
        return milestoneService.createMilestone(pfeId, request);
    }

    @PostMapping("/pfes/{pfeId}/milestones/bulk")
    public List<Milestone> createMilestonesBulk(
            @PathVariable Long pfeId,
            @Valid @RequestBody CreateMilestonesBulkRequest request
    ) {
        return milestoneService.createMilestonesBulk(pfeId, request);
    }

    @PutMapping("/milestones/{milestoneId}")
    public Milestone updateMilestone(
            @PathVariable Long milestoneId,
            @RequestBody UpdateMilestoneRequest request
    ) {
        return milestoneService.updateMilestone(milestoneId, request);
    }
}