package com.pfetracker.service.module1;

import com.pfetracker.dto.module1.*;
import java.util.List;

import org.springframework.stereotype.Service;
@Service("adminServiceM1")

public interface AdminService {

 //Gestion des comptes

 void creerCompte(CreerCompteRequest request);

 void supprimerCompte(Long userId);

 void toggleCompte(Long userId, boolean activer);

 void deverrouillerCompte(Long userId);

 void reinitialiserMotDePasse(Long userId);

 void changerRole(Long userId, String nouveauRole);

 List<UtilisateurResponse> getTousLesComptes();

 List<UtilisateurResponse> getComptesByRole(String role);

 List<UtilisateurResponse> getComptesVerrouilles();

 // Gestion des départements 

 void creerDepartement(DepartementRequest request);

 void modifierDepartement(Long deptId, DepartementRequest request);

 void supprimerDepartement(Long deptId);

 List<DepartementResponse> getTousDepartements();

 void assignerChef(Long deptId, Long userId);

 // Affectations globales

 void forcerAffectationGlobale(Long etudiantId, Long encadrantId);

 void rompreAffectation(Long etudiantId);

 void transfererEtudiant(Long etudiantId, Long nouveauDeptId);

 // Supervision globale 

 DashboardGlobalResponse getDashboardComplet();

 List<LogAuditResponse> getTousLesLogs();

 void reinitialiserSysteme();



}