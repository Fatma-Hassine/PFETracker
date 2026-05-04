package com.pfetracker.controller.module2;

import com.pfetracker.entity.module2.AiAlert;
import com.pfetracker.repository.module2.AiAlertRepository;
import com.pfetracker.service.module2.AiDelayDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller des alertes IA.
 *
 * Ici, l'IA actuelle est un moteur de règles :
 * - tâche en retard
 * - jalon en retard
 * - stagnation
 * - PFE à risque
 * - sprint non clôturé
 */
@RestController
@RequestMapping("/api/v2/pfeTrack")
@RequiredArgsConstructor
public class AiAlertController {

    private final AiAlertRepository aiAlertRepository;
    private final AiDelayDetectionService aiDelayDetectionService;

    /**
     * Liste les alertes actives d'un PFE.
     */
    @GetMapping("/pfes/{pfeId}/alerts")
    public List<AiAlert> getActiveAlerts(@PathVariable Long pfeId) {
        return aiAlertRepository.findByPfeIdAndResolvedFalseOrderByCreatedAtDesc(pfeId);
    }

    /**
     * Lance manuellement la détection IA.
     * Utile pour tester sans attendre le @Scheduled.
     */
    @PostMapping("/ai/run-detection")
    public String runDetection() {
        aiDelayDetectionService.runDetectionManually();
        return "Détection IA exécutée avec succès";
    }

    /**
     * Marque une alerte comme résolue.
     */
    @PatchMapping("/alerts/{alertId}/resolve")
    public AiAlert resolveAlert(@PathVariable Long alertId) {
        AiAlert alert = aiAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alerte introuvable avec id = " + alertId));

        alert.setResolved(true);

        return aiAlertRepository.save(alert);
    }
}