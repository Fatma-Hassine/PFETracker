package com.pfetracker.service.module2;

import com.pfetracker.dto.module2.CreateMilestoneRequest;
import com.pfetracker.dto.module2.CreateMilestonesBulkRequest;
import com.pfetracker.dto.module2.UpdateMilestoneRequest;
import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.enums.MilestoneStatus;
import com.pfetracker.repository.module2.MilestoneRepository;
import com.pfetracker.repository.module2.PfeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final PfeRepository pfeRepository;
    private final ProgressService progressService;

    public List<Milestone> getMilestonesByPfe(Long pfeId) {
        return milestoneRepository.findByPfeIdOrderByOrderIndexAsc(pfeId);
    }

    public Milestone getMilestoneById(Long milestoneId) {
        return milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Jalon introuvable avec id = " + milestoneId));
    }

    @Transactional
    public Milestone createMilestone(Long pfeId, CreateMilestoneRequest request) {
        Pfe pfe = pfeRepository.findById(pfeId)
                .orElseThrow(() -> new RuntimeException("PFE introuvable avec id = " + pfeId));

        List<Milestone> existingMilestones = milestoneRepository.findByPfeIdOrderByOrderIndexAsc(pfeId);

        Milestone milestone = new Milestone();
        milestone.setPfe(pfe);
        milestone.setOrderIndex(existingMilestones.size() + 1);
        milestone.setTitle(request.getTitle());
        milestone.setDescription(request.getDescription());
        milestone.setExpectedDeliverable(request.getExpectedDeliverable());
        milestone.setPlannedStartDate(request.getPlannedStartDate());
        milestone.setPlannedEndDate(request.getPlannedEndDate());
        milestone.setWeight(request.getWeight() != null ? request.getWeight() : 1.0);
        milestone.setProgress(0.0);
        milestone.setStatus(MilestoneStatus.NOT_STARTED);

        Milestone saved = milestoneRepository.save(milestone);

        progressService.recalculatePfeProgress(pfeId);

        return saved;
    }

    @Transactional
    public List<Milestone> createMilestonesBulk(Long pfeId, CreateMilestonesBulkRequest request) {
        List<Milestone> createdMilestones = request.getMilestones()
                .stream()
                .map(item -> createMilestone(pfeId, item))
                .toList();

        progressService.recalculatePfeProgress(pfeId);

        return createdMilestones;
    }

    @Transactional
    public Milestone updateMilestone(Long milestoneId, UpdateMilestoneRequest request) {
        Milestone milestone = getMilestoneById(milestoneId);

        if (request.getPlannedStartDate() != null) {
            milestone.setPlannedStartDate(request.getPlannedStartDate());
        }

        if (request.getPlannedEndDate() != null) {
            milestone.setPlannedEndDate(request.getPlannedEndDate());
        }

        if (request.getWeight() != null) {
            milestone.setWeight(request.getWeight());
        }

        milestone.setSupervisorComment(request.getSupervisorComment());

        Milestone saved = milestoneRepository.save(milestone);

        progressService.recalculatePfeProgress(saved.getPfe().getId());

        return saved;
    }
}