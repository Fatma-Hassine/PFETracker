package com.pfetracker.service.module2;

import com.pfetracker.dto.module2.CreatePfeRequest;
import com.pfetracker.dto.module2.UpdateProjectSheetRequest;
import com.pfetracker.dto.module2.ValidateProjectSheetRequest;
import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.enums.PfeStatus;
import com.pfetracker.repository.module2.MilestoneRepository;
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
    private final MilestoneRepository milestoneRepository;
    private final MilestoneFactory milestoneFactory;
    private final CurrentUserService currentUserService;

    @Transactional
    public Pfe createPfe(CreatePfeRequest request) {
        // Un étudiant n'a qu'un seul PFE
        if (request.getStudentId() != null
                && pfeRepository.findFirstByStudentId(request.getStudentId()).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Cet étudiant a déjà un PFE");
        }

        Pfe pfe = new Pfe();

        pfe.setStudentId(request.getStudentId());
        pfe.setStudentName(request.getStudentName());
        pfe.setStudentEmail(request.getStudentEmail());

        pfe.setSupervisorId(request.getSupervisorId());
        pfe.setSupervisorName(request.getSupervisorName());
        pfe.setSupervisorEmail(request.getSupervisorEmail());

        pfe.setDepartment(request.getDepartment());

        pfe.setTitle(request.getTitle());
        pfe.setDescription(request.getDescription());
        pfe.setProblemStatement(request.getProblemStatement());
        pfe.setObjectives(request.getObjectives());
        pfe.setTechnologies(request.getTechnologies());

        pfe.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now());
        pfe.setDefenseDate(request.getDefenseDate());

        pfe.setStatus(PfeStatus.IN_PROGRESS);
        pfe.setProgress(0.0);

        Pfe saved = pfeRepository.save(pfe);

        // MODIF : cahier des charges §5.1.1 — "Le PFE est initialisé avec les
        // 6 jalons prédéfinis dans l'ordre chronologique". MilestoneFactory
        // existait déjà mais n'était jamais appelée, laissant chaque PFE sans
        // aucun jalon tant que l'étudiant n'en créait pas manuellement.
        // L'étudiant peut toujours ajuster les dates/poids ensuite, ou
        // demander à l'assistant IA de proposer des tâches à l'intérieur.
        List<Milestone> jalons = milestoneFactory.createDefaultMilestones(saved);
        milestoneRepository.saveAll(jalons);

        return saved;
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