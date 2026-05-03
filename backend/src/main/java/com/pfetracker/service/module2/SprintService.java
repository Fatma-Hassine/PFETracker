package com.pfetracker.service.module2;

import com.pfetracker.dto.module2.CreateSprintRequest;
import com.pfetracker.dto.module2.SubmitSprintReviewRequest;
import com.pfetracker.dto.module2.ValidateSprintRequest;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.Sprint;
import com.pfetracker.entity.module2.enums.SprintStatus;
import com.pfetracker.repository.module2.PfeRepository;
import com.pfetracker.repository.module2.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final PfeRepository pfeRepository;

    public List<Sprint> getSprintsByPfe(Long pfeId) {
        return sprintRepository.findByPfeIdOrderByStartDateDesc(pfeId);
    }

    public Sprint getSprintById(Long sprintId) {
        return sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint introuvable avec id = " + sprintId));
    }

    @Transactional
    public Sprint createSprint(Long pfeId, CreateSprintRequest request) {
        Pfe pfe = pfeRepository.findById(pfeId)
                .orElseThrow(() -> new RuntimeException("PFE introuvable avec id = " + pfeId));

        Sprint sprint = new Sprint();
        sprint.setPfe(pfe);
        sprint.setObjective(request.getObjective());
        sprint.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now());
        sprint.setEndDate(request.getEndDate() != null ? request.getEndDate() : LocalDate.now().plusWeeks(1));
        sprint.setStatus(SprintStatus.IN_PROGRESS);

        return sprintRepository.save(sprint);
    }

    @Transactional
    public Sprint submitSprintReview(Long sprintId, SubmitSprintReviewRequest request) {
        Sprint sprint = getSprintById(sprintId);

        sprint.setStudentReport(request.getStudentReport());
        sprint.setStatus(SprintStatus.REVIEW_SUBMITTED);

        return sprintRepository.save(sprint);
    }

    @Transactional
    public Sprint validateSprint(Long sprintId, ValidateSprintRequest request) {
        Sprint sprint = getSprintById(sprintId);

        sprint.setSupervisorComment(request.getSupervisorComment());

        if (Boolean.TRUE.equals(request.getAccepted())) {
            sprint.setStatus(SprintStatus.VALIDATED);
        } else {
            sprint.setStatus(SprintStatus.IN_PROGRESS);
        }

        return sprintRepository.save(sprint);
    }

    @Transactional
    public Sprint closeSprint(Long sprintId) {
        Sprint sprint = getSprintById(sprintId);

        sprint.setStatus(SprintStatus.CLOSED);

        return sprintRepository.save(sprint);
    }
}