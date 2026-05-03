package com.pfetracker.service.module2;

import com.pfetracker.dto.module2.UpdateMilestoneRequest;
import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.repository.module2.MilestoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProgressService progressService;

    public List<Milestone> getMilestonesByPfe(Long pfeId) {
        return milestoneRepository.findByPfeIdOrderByOrderIndexAsc(pfeId);
    }

    public Milestone getMilestoneById(Long milestoneId) {
        return milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Jalon introuvable avec id = " + milestoneId));
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