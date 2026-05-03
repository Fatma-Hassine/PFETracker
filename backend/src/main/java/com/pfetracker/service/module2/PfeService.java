package com.pfetracker.service.module2;

import com.pfetracker.dto.module2.CreatePfeRequest;
import com.pfetracker.dto.module2.UpdateProjectSheetRequest;
import com.pfetracker.dto.module2.ValidateProjectSheetRequest;
import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.enums.PfeStatus;
import com.pfetracker.repository.module2.PfeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PfeService {

    private final PfeRepository pfeRepository;
    private final MilestoneFactory milestoneFactory;
    private final CurrentUserService currentUserService;

    @Transactional
    public Pfe createPfe(CreatePfeRequest request) {
        Pfe pfe = new Pfe();

        pfe.setStudentId(request.getStudentId());
        pfe.setSupervisorId(request.getSupervisorId());
        pfe.setTitle(request.getTitle());
        pfe.setDescription(request.getDescription());
        pfe.setProblemStatement(request.getProblemStatement());
        pfe.setObjectives(request.getObjectives());
        pfe.setTechnologies(request.getTechnologies());
        pfe.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now());
        pfe.setDefenseDate(request.getDefenseDate());
        pfe.setStatus(PfeStatus.IN_PROGRESS);
        pfe.setProgress(0.0);

        List<Milestone> milestones = milestoneFactory.createDefaultMilestones(pfe);
        pfe.setMilestones(milestones);

        return pfeRepository.save(pfe);
    }

    public List<Pfe> getMyPfes() {
        Long userId = currentUserService.getCurrentUserId();
        String role = currentUserService.getCurrentUserRole();

        if ("STUDENT".equals(role)) {
            return pfeRepository.findByStudentId(userId);
        }

        if ("SUPERVISOR".equals(role) || "ENCADRANT".equals(role)) {
            return pfeRepository.findBySupervisorId(userId);
        }

        return pfeRepository.findAll();
    }

    public Pfe getPfeById(Long pfeId) {
        return pfeRepository.findById(pfeId)
                .orElseThrow(() -> new RuntimeException("PFE introuvable avec id = " + pfeId));
    }

    @Transactional
    public Pfe updateProjectSheet(Long pfeId, UpdateProjectSheetRequest request) {
        Pfe pfe = getPfeById(pfeId);

        pfe.setTitle(request.getTitle());
        pfe.setDescription(request.getDescription());
        pfe.setProblemStatement(request.getProblemStatement());
        pfe.setObjectives(request.getObjectives());
        pfe.setTechnologies(request.getTechnologies());
        pfe.setStartDate(request.getStartDate());
        pfe.setDefenseDate(request.getDefenseDate());

        return pfeRepository.save(pfe);
    }

    @Transactional
    public Pfe validateProjectSheet(Long pfeId, ValidateProjectSheetRequest request) {
        Pfe pfe = getPfeById(pfeId);

        boolean accepted = Boolean.TRUE.equals(request.getAccepted());

        pfe.setFicheValidationComment(request.getComment());
        pfe.setStatus(accepted ? PfeStatus.IN_PROGRESS : PfeStatus.INITIALIZED);

        return pfeRepository.save(pfe);
    }
}