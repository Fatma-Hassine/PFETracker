package com.pfetracker.controller.module1;
import com.pfetracker.dto.module1.*;
import com.pfetracker.service.module1.DirecteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("directeurControllerM1")
@RequestMapping("/directeur")
@PreAuthorize("hasRole('ROLE_DIRECTEUR')")
@RequiredArgsConstructor
public class DirecteurController {
	private final DirecteurService directeurService;


    @GetMapping("/etudiants")
    public ResponseEntity<List<EtudiantStageResponse>> getTousLesEtudiants() {
        return ResponseEntity.ok(directeurService.getTousLesEtudiants());
    }

    @GetMapping("/etudiants/non-affectes")
    public ResponseEntity<List<EtudiantStageResponse>> getNonAffectes() {
        return ResponseEntity.ok(directeurService.getEtudiantsNonAffectes());
    }

    @GetMapping("/etudiants/alerte")
    public ResponseEntity<List<EtudiantStageResponse>> getEnAlerte() {
        return ResponseEntity.ok(directeurService.getEtudiantsEnAlerte());
    }

    @GetMapping("/stages/proches-expiration")
    public ResponseEntity<List<EtudiantStageResponse>> getProchesExpiration() {
        return ResponseEntity.ok(directeurService.getStagesProchesExpiration());
    }

    @GetMapping("/stages/expires")
    public ResponseEntity<List<EtudiantStageResponse>> getExpires() {
        return ResponseEntity.ok(directeurService.getStagesExpires());
    }


    @GetMapping("/dashboard")
    public ResponseEntity<DirecteurDashboardResponse> getDashboard() {
        return ResponseEntity.ok(directeurService.getDashboard());
    }


    // Lancer la vérification + affectation automatique après 21 jours
    @PostMapping("/affectations/verifier")
    public ResponseEntity<Void> verifierEtAffecter() {
        directeurService.verifierEtAffecterApresDelai();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/affectations/affecter")
    public ResponseEntity<Void> affecterManuellement(
            @RequestParam Long etudiantId,
            @RequestParam Long encadrantId) {
        directeurService.affecterManuellement(etudiantId, encadrantId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/affectations/reaffecter")
    public ResponseEntity<Void> reaffecter(
            @RequestParam Long etudiantId,
            @RequestParam Long nouvelEncadrantId) {
        directeurService.reaffecter(etudiantId, nouvelEncadrantId);
        return ResponseEntity.ok().build();
    }


    
}
