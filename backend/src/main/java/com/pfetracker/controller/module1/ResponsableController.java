package com.pfetracker.controller.module1;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfetracker.dto.module1.AffectationImportResultDTO;
import com.pfetracker.dto.module1.DashboardDepartementResponse;
import com.pfetracker.dto.module1.EncadrantChargeResponse;
import com.pfetracker.dto.module1.EtudiantResponse;
import com.pfetracker.dto.module1.UtilisateurResponse;
import com.pfetracker.entity.module1.ChefDepartement;
import com.pfetracker.exception.module1.BusinessException;
import com.pfetracker.repository.module1.ChefDepartementRepository;
import com.pfetracker.service.module1.ExportService;
import com.pfetracker.service.module1.ResponsableService;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@RestController("responsableControllerM1")
@RequestMapping("/responsable")
@PreAuthorize("hasAuthority('ROLE_CHEF_DEPARTEMENT')")
@RequiredArgsConstructor
public class ResponsableController {
	private final ResponsableService responsableService;
	private final ExportService exportService;
	private final ChefDepartementRepository chefDepartementRepo;

	/**
	 * MODIF : isolation stricte entre départements (cahier des charges §4.1.3).
	 * Sans cette vérification, un chef de département authentifié pouvait
	 * appeler /responsable/{autreDeptId}/... et consulter/modifier un autre
	 * département — rien ne comparait le {deptId} de l'URL à son propre
	 * département.
	 */
	private void verifierAppartenance(Long userId, Long deptId) {
		ChefDepartement chef = chefDepartementRepo.findById(userId)
				.orElseThrow(() -> new BusinessException("Chef de département introuvable"));

		if (chef.getDepartement() == null || !chef.getDepartement().getId().equals(deptId)) {
			throw new org.springframework.security.access.AccessDeniedException(
					"Vous n'avez pas accès à ce département");
		}
	}

    @GetMapping("/{deptId}/comptes/en-attente")
    public ResponseEntity<List<UtilisateurResponse>> getComptesEnAttente(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long deptId) {
        verifierAppartenance(userId, deptId);
        return ResponseEntity.ok(responsableService.getComptesEnAttente(deptId));
    }

    @GetMapping("/{deptId}/comptes")
    public ResponseEntity<List<UtilisateurResponse>> getComptesDepartement(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long deptId) {
        verifierAppartenance(userId, deptId);
        return ResponseEntity.ok(responsableService.getComptesDepartement(deptId));
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
            @AuthenticationPrincipal Long userId,
            @PathVariable Long deptId) {
        verifierAppartenance(userId, deptId);
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

    // Import en masse des affectations encadrant-étudiant à partir d'un fichier
    // (.xlsx ou .csv) fourni par le chef de département — 2 colonnes :
    // email étudiant, email ou code encadrant.
    @PostMapping(value = "/{deptId}/affectations/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AffectationImportResultDTO> importerAffectations(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long deptId,
            @RequestParam("fichier") MultipartFile fichier) {
        verifierAppartenance(userId, deptId);
        return ResponseEntity.ok(responsableService.importerAffectations(deptId, fichier));
    }

    @GetMapping("/{deptId}/encadrants/charge")
    public ResponseEntity<List<EncadrantChargeResponse>> getChargeEncadrants(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long deptId) {
        verifierAppartenance(userId, deptId);
        return ResponseEntity.ok(responsableService.getChargeEncadrants(deptId));
    }

    @PatchMapping("/{deptId}/limite-etudiants")
    public ResponseEntity<Void> configurerLimite(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long deptId,
            @RequestParam int limite) {
        verifierAppartenance(userId, deptId);
        responsableService.configurerLimiteEtudiants(deptId, limite);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{deptId}/dashboard")
    public ResponseEntity<DashboardDepartementResponse> getDashboard(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long deptId) {
        verifierAppartenance(userId, deptId);
        return ResponseEntity.ok(responsableService.getDashboard(deptId));
    }

    @GetMapping("/{deptId}/export/excel")
    public ResponseEntity<byte[]> exporterExcel(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long deptId) {
        verifierAppartenance(userId, deptId);
        byte[] fichier = exportService.exporterDepartementExcel(deptId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=departement-" + deptId + ".xlsx")
                .body(fichier);
    }

    @GetMapping("/{deptId}/export/pdf")
    public ResponseEntity<byte[]> exporterPdf(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long deptId) {
        verifierAppartenance(userId, deptId);
        byte[] fichier = exportService.exporterDepartementPdf(deptId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-departement-" + deptId + ".pdf")
                .body(fichier);
    }



    @PostMapping("/encadrants/{encadrantId}/regenerer-code")
    public ResponseEntity<Void> regenererCode(@PathVariable Long encadrantId) {
        responsableService.regenererCodeEncadrant(encadrantId);
        return ResponseEntity.ok().build();
    }
}
