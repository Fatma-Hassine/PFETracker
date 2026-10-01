package com.pfetracker.service.module2;

import com.pfetracker.dto.module2.StudentWeeklySummaryDTO;
import com.pfetracker.dto.module2.WeeklyReportDTO;
import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.entity.module1.enums.Role;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.entity.module2.enums.TaskStatus;
import com.pfetracker.repository.module1.UtilisateurRepository;
import com.pfetracker.repository.module2.AiAlertRepository;
import com.pfetracker.repository.module2.Module2TaskRepository;
import com.pfetracker.repository.module2.PfeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rapport hebdomadaire automatique pour chaque encadrant (cahier des charges
 * §5.7.2) : envoyé par email chaque lundi matin et disponible dans le
 * tableau de bord (voir PfeController#getRapportHebdomadaire).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final PfeRepository pfeRepository;
    private final Module2TaskRepository taskRepository;
    private final AiAlertRepository aiAlertRepository;
    private final UtilisateurRepository utilisateurRepo;
    private final JavaMailSender mailSender;

    @Scheduled(cron = "0 0 7 * * MON")
    public void envoyerRapportsHebdomadaires() {
        List<Utilisateur> encadrants = utilisateurRepo.findByRole(Role.ROLE_ENCADRANT);

        for (Utilisateur encadrant : encadrants) {
            WeeklyReportDTO rapport = genererRapport(encadrant.getId());
            if (rapport.getEtudiants().isEmpty()) {
                continue;
            }
            try {
                envoyerParEmail(encadrant.getEmail(), rapport);
            } catch (Exception e) {
                log.warn("Envoi du rapport hebdomadaire échoué pour {} : {}",
                        encadrant.getEmail(), e.getMessage());
            }
        }
    }

    public WeeklyReportDTO genererRapport(Long encadrantId) {
        Utilisateur encadrant = utilisateurRepo.findById(encadrantId).orElse(null);
        List<Pfe> pfes = pfeRepository.findBySupervisorId(encadrantId);
        LocalDateTime depuisUneSemaine = LocalDateTime.now().minusDays(7);

        List<StudentWeeklySummaryDTO> resumes = pfes.stream()
                .map(pfe -> construireResume(pfe, depuisUneSemaine))
                .collect(Collectors.toList());

        return new WeeklyReportDTO(
                encadrantId,
                encadrant != null ? encadrant.getNomComplet() : null,
                LocalDate.now(),
                resumes
        );
    }

    private StudentWeeklySummaryDTO construireResume(Pfe pfe, LocalDateTime depuisUneSemaine) {
        List<Task> taches = taskRepository.findByMilestonePfeId(pfe.getId());

        int valideesCetteSemaine = (int) taches.stream()
                .filter(t -> t.getStatus() == TaskStatus.VALIDATED
                        && t.getUpdatedAt() != null
                        && t.getUpdatedAt().isAfter(depuisUneSemaine))
                .count();

        int enRetard = (int) taches.stream()
                .filter(t -> t.getDeadline() != null
                        && t.getDeadline().isBefore(LocalDate.now())
                        && t.getStatus() != TaskStatus.VALIDATED
                        && t.getStatus() != TaskStatus.CANCELLED)
                .count();

        int alertes = aiAlertRepository.findByPfeIdAndResolvedFalseOrderByCreatedAtDesc(pfe.getId()).size();

        return new StudentWeeklySummaryDTO(
                pfe.getStudentId(),
                pfe.getStudentName(),
                pfe.getTitle(),
                pfe.getProgress() != null ? pfe.getProgress() : 0.0,
                valideesCetteSemaine,
                enRetard,
                alertes
        );
    }

    private void envoyerParEmail(String email, WeeklyReportDTO rapport) {
        StringBuilder corps = new StringBuilder();
        corps.append("Rapport hebdomadaire — semaine du ").append(rapport.getGenereLe()).append("\n\n");

        for (StudentWeeklySummaryDTO e : rapport.getEtudiants()) {
            corps.append("• ").append(e.getStudentName())
                    .append(" — ").append(e.getPfeTitle()).append("\n")
                    .append("   Progression : ").append(Math.round(e.getProgression())).append("%\n")
                    .append("   Tâches validées cette semaine : ").append(e.getTachesValideesSemaine()).append("\n")
                    .append("   Tâches en retard : ").append(e.getTachesEnRetard()).append("\n")
                    .append("   Alertes actives : ").append(e.getAlertesActives()).append("\n\n");
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("PFETracker — Rapport hebdomadaire de vos étudiants");
        msg.setText(corps.toString());
        mailSender.send(msg);
    }
}
