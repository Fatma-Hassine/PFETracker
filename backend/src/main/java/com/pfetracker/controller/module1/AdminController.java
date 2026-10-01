package com.pfetracker.controller.module1;
import com.pfetracker.dto.module1.*;
import com.pfetracker.service.module1.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminControllerM1")
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {
	 private final AdminService adminService;

	    
	    @PostMapping("/comptes")
	    public ResponseEntity<Void> creerCompte(
	            @RequestBody CreerCompteRequest request) {
	        adminService.creerCompte(request);
	        return ResponseEntity.status(HttpStatus.CREATED).build();
	    }

	    @DeleteMapping("/comptes/{userId}")
	    public ResponseEntity<Void> supprimerCompte(@PathVariable Long userId) {
	        adminService.supprimerCompte(userId);
	        return ResponseEntity.noContent().build();
	    }

	    @PatchMapping("/comptes/{userId}/activation")
	    public ResponseEntity<Void> toggleCompte(
	            @PathVariable Long userId,
	            @RequestParam boolean activer) {
	        adminService.toggleCompte(userId, activer);
	        return ResponseEntity.ok().build();
	    }

	    @PostMapping("/comptes/{userId}/deverrouiller")
	    public ResponseEntity<Void> deverrouiller(@PathVariable Long userId) {
	        adminService.deverrouillerCompte(userId);
	        return ResponseEntity.ok().build();
	    }

	    @PostMapping("/comptes/{userId}/reinitialiser-mdp")
	    public ResponseEntity<Void> reinitialiserMdp(@PathVariable Long userId) {
	        adminService.reinitialiserMotDePasse(userId);
	        return ResponseEntity.ok().build();
	    }

	    @PatchMapping("/comptes/{userId}/role")
	    public ResponseEntity<Void> changerRole(
	            @PathVariable Long userId,
	            @RequestParam String role) {
	        adminService.changerRole(userId, role);
	        return ResponseEntity.ok().build();
	    }

	    @GetMapping("/comptes")
	    public ResponseEntity<List<UtilisateurResponse>> getTousLesComptes() {
	        return ResponseEntity.ok(adminService.getTousLesComptes());
	    }

	    @GetMapping("/comptes/role/{role}")
	    public ResponseEntity<List<UtilisateurResponse>> getParRole(
	            @PathVariable String role) {
	        return ResponseEntity.ok(adminService.getComptesByRole(role));
	    }

	    @GetMapping("/comptes/verrouilles")
	    public ResponseEntity<List<UtilisateurResponse>> getVerrouilles() {
	        return ResponseEntity.ok(adminService.getComptesVerrouilles());
	    }

	   
	    @PostMapping("/departements")
	    public ResponseEntity<Void> creerDepartement(
	            @RequestBody DepartementRequest request) {
	        adminService.creerDepartement(request);
	        return ResponseEntity.status(HttpStatus.CREATED).build();
	    }

	    @PutMapping("/departements/{deptId}")
	    public ResponseEntity<Void> modifierDepartement(
	            @PathVariable Long deptId,
	            @RequestBody DepartementRequest request) {
	        adminService.modifierDepartement(deptId, request);
	        return ResponseEntity.ok().build();
	    }

	    @DeleteMapping("/departements/{deptId}")
	    public ResponseEntity<Void> supprimerDepartement(@PathVariable Long deptId) {
	        adminService.supprimerDepartement(deptId);
	        return ResponseEntity.noContent().build();
	    }

	    @GetMapping("/departements")
	    public ResponseEntity<List<DepartementResponse>> getDepartements() {
	        return ResponseEntity.ok(adminService.getTousDepartements());
	    }

	    @PostMapping("/departements/{deptId}/chef/{userId}")
	    public ResponseEntity<Void> assignerChef(
	            @PathVariable Long deptId,
	            @PathVariable Long userId) {
	        adminService.assignerChef(deptId, userId);
	        return ResponseEntity.ok().build();
	    }

	    @PostMapping("/affectations/forcer")
	    public ResponseEntity<Void> forcerAffectation(
	            @RequestParam Long etudiantId,
	            @RequestParam Long encadrantId) {
	        adminService.forcerAffectationGlobale(etudiantId, encadrantId);
	        return ResponseEntity.ok().build();
	    }

	    @DeleteMapping("/affectations/{etudiantId}")
	    public ResponseEntity<Void> rompreAffectation(@PathVariable Long etudiantId) {
	        adminService.rompreAffectation(etudiantId);
	        return ResponseEntity.noContent().build();
	    }

	    @PatchMapping("/etudiants/{etudiantId}/transfert")
	    public ResponseEntity<Void> transfererEtudiant(
	            @PathVariable Long etudiantId,
	            @RequestParam Long nouveauDeptId) {
	        adminService.transfererEtudiant(etudiantId, nouveauDeptId);
	        return ResponseEntity.ok().build();
	    }

	    @GetMapping("/dashboard")
	    public ResponseEntity<DashboardGlobalResponse> getDashboard() {
	        return ResponseEntity.ok(adminService.getDashboardComplet());
	    }

	    @GetMapping("/logs")
	    public ResponseEntity<List<LogAuditResponse>> getLogs() {
	        return ResponseEntity.ok(adminService.getTousLesLogs());
	    }

	    @PostMapping("/systeme/reinitialiser")
	    public ResponseEntity<Void> reinitialiserSysteme() {
	        adminService.reinitialiserSysteme();
	        return ResponseEntity.ok().build();
	    }

	    
}
