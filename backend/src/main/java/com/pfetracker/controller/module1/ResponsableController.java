package com.pfetracker.controller.module1;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfetracker.dto.module1.DashboardDepartementResponse;
import com.pfetracker.dto.module1.EncadrantChargeResponse;
import com.pfetracker.dto.module1.EtudiantResponse;
import com.pfetracker.dto.module1.UtilisateurResponse;
import com.pfetracker.service.module1.ResponsableService;
import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;

@RestController("responsableControllerM1")
@RequestMapping("/api/responsable")
@PreAuthorize("hasRole('ROLE_DEPT_MANAGER')")
@RequiredArgsConstructor
public class ResponsableController {
	private final ResponsableService responsableService;

  
    @GetMapping("/{deptId}/comptes/en-attente")
    public ResponseEntity<List<UtilisateurResponse>> getComptesEnAttente(
            @PathVariable Long deptId) {
        return ResponseEntity.ok(responsableService.getComptesEnAttente(deptId));
    }

    @PostMapping("/comptes/{userId}/valider")
    public ResponseEntity<Void> validerCompte(@PathVariable Long userId) {
        responsableService.validerCompte(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comptes/{userId}/refuser")
    public ResponseEntity<Void> refuserCompte(
            @PathVariable Long userId,
            @RequestParam String motif) {
        responsableService.refuserCompte(userId, motif);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/comptes/{userId}/activation")
    public ResponseEntity<Void> activerDesactiver(
            @PathVariable Long userId,
            @RequestParam boolean activer) {
        responsableService.activerDesactiverCompte(userId, activer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comptes/{userId}/deverrouiller")
    public ResponseEntity<Void> deverrouiller(@PathVariable Long userId) {
        responsableService.deverrouillerCompte(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comptes/{userId}/reinitialiser-mdp")
    public ResponseEntity<Void> reinitialiserMdp(@PathVariable Long userId) {
        responsableService.reinitialiserMotDePasseForce(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/comptes/{userId}/departement")
    public ResponseEntity<Void> changerDepartement(
            @PathVariable Long userId,
            @RequestParam Long nouveauDeptId) {
        responsableService.changerDepartement(userId, nouveauDeptId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{deptId}/etudiants/orphelins")
    public ResponseEntity<List<EtudiantResponse>> getOrphelins(
            @PathVariable Long deptId) {
        return ResponseEntity.ok(responsableService.getEtudiantsSansEncadrant(deptId));
    }

    @PostMapping("/affectations/forcer")
    public ResponseEntity<Void> forcerAffectation(
            @RequestParam Long etudiantId,
            @RequestParam Long encadrantId) {
        responsableService.forcerAffectation(etudiantId, encadrantId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/affectations/{etudiantId}")
    public ResponseEntity<Void> rompreAffectation(@PathVariable Long etudiantId) {
        responsableService.rompreAffectation(etudiantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{deptId}/encadrants/charge")
    public ResponseEntity<List<EncadrantChargeResponse>> getChargeEncadrants(
            @PathVariable Long deptId) {
        return ResponseEntity.ok(responsableService.getChargeEncadrants(deptId));
    }

    @PatchMapping("/{deptId}/limite-etudiants")
    public ResponseEntity<Void> configurerLimite(
            @PathVariable Long deptId,
            @RequestParam int limite) {
        responsableService.configurerLimiteEtudiants(deptId, limite);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{deptId}/dashboard")
    public ResponseEntity<DashboardDepartementResponse> getDashboard(
            @PathVariable Long deptId) {
        return ResponseEntity.ok(responsableService.getDashboard(deptId));
    }



    @PostMapping("/encadrants/{encadrantId}/regenerer-code")
    public ResponseEntity<Void> regenererCode(@PathVariable Long encadrantId) {
        responsableService.regenererCodeEncadrant(encadrantId);
        return ResponseEntity.ok().build();
    }
}
