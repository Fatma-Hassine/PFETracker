package com.pfetracker.service.impl.module1;
import com.pfetracker.config.module1.AppProperties;
import com.pfetracker.dto.module1.*;
import com.pfetracker.entity.module1.*;
import com.pfetracker.entity.module1.enums.Role;
import com.pfetracker.exception.module1.*;
import com.pfetracker.repository.module1.*;
import com.pfetracker.service.module1.AdminService;
import com.pfetracker.service.module1.LogAuditService;
import com.pfetracker.service.module1.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService{
	 private final UtilisateurRepository utilisateurRepo;
	    private final EtudiantRepository etudiantRepo;
	    private final EncadrantRepository encadrantRepo;
	    private final DepartementRepository departementRepo;
	    private final ChefDepartementRepository chefDeptRepo;
	    private final RefreshTokenRepository refreshTokenRepo;
	    private final LogAuditRepository logAuditRepo;
	    private final PasswordEncoder passwordEncoder;
	    private final NotificationService notificationService;
	    private final LogAuditService logAuditService;
	    private final AppProperties appProperties;

	    //  Gestion des comptes 

	    @Override
	    public void creerCompte(CreerCompteRequest req) {

	        if (utilisateurRepo.existsByEmail(req.getEmail())) {
	            throw new EmailAlreadyExistsException(req.getEmail());
	        }

	        String mdpTemp = genererMotDePasse();
	        Utilisateur user = creerUtilisateurSelon(req, mdpTemp);
	        utilisateurRepo.save(user);

	        notificationService.envoyerEmailBienvenue(user.getEmail(), mdpTemp);
	        logAuditService.log(user, "CREATION_COMPTE", null, "SUCCES",
	                "Rôle : " + req.getRole());
	    }

	    @Override
	    public void supprimerCompte(Long userId) {

	        Utilisateur user = utilisateurRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));

	        // tokens avant suppression
	        refreshTokenRepo.revoquerTousParUtilisateur(userId);

	        logAuditService.log(user, "SUPPRESSION_COMPTE", null, "SUCCES", null);
	        utilisateurRepo.delete(user);
	    }

	    @Override
	    public void toggleCompte(Long userId, boolean activer) {

	        Utilisateur user = utilisateurRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));

	        user.setEnabled(activer);
	        utilisateurRepo.save(user);

	        logAuditService.log(user,
	                activer ? "ACTIVATION_COMPTE" : "DESACTIVATION_COMPTE",
	                null, "SUCCES", null);
	    }

	    @Override
	    public void deverrouillerCompte(Long userId) {

	        Utilisateur user = utilisateurRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));

	        user.setAccountLocked(false);
	        user.setFailedLoginAttempts(0);
	        user.setLockTime(null);
	        utilisateurRepo.save(user);

	        logAuditService.log(user, "DEVERROUILLAGE_COMPTE", null, "SUCCES", null);
	    }

	    @Override
	    public void reinitialiserMotDePasse(Long userId) {

	        Utilisateur user = utilisateurRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));

	        String mdpTemp = genererMotDePasse();
	        user.setMotDePasse(passwordEncoder.encode(mdpTemp));
	        user.setMustChangePassword(true);
	        utilisateurRepo.save(user);

	        notificationService.envoyerEmailBienvenue(user.getEmail(), mdpTemp);
	        logAuditService.log(user, "REINITIALISATION_MDP", null, "SUCCES", null);
	    }

	    @Override
	    public void changerRole(Long userId, String nouveauRole) {

	        Utilisateur user = utilisateurRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));

	        try {
	            Role role = Role.valueOf(nouveauRole);
	            user.setRole(role);
	            utilisateurRepo.save(user);
	            logAuditService.log(user, "CHANGEMENT_ROLE", null, "SUCCES",
	                    "Nouveau rôle : " + nouveauRole);
	        } catch (IllegalArgumentException e) {
	            throw new BusinessException("Rôle invalide : " + nouveauRole);
	        }
	    }

	    @Override
	    public List<UtilisateurResponse> getTousLesComptes() {
	        return utilisateurRepo.findAll()
	                .stream()
	                .map(UtilisateurResponse::fromEntity)
	                .collect(Collectors.toList());
	    }

	    @Override
	    public List<UtilisateurResponse> getComptesByRole(String role) {
	        try {
	            return utilisateurRepo.findByRole(Role.valueOf(role))
	                    .stream()
	                    .map(UtilisateurResponse::fromEntity)
	                    .collect(Collectors.toList());
	        } catch (IllegalArgumentException e) {
	            throw new BusinessException("Rôle invalide : " + role);
	        }
	    }

	    @Override
	    public List<UtilisateurResponse> getComptesVerrouilles() {
	        return utilisateurRepo.findByAccountLockedTrue()
	                .stream()
	                .map(UtilisateurResponse::fromEntity)
	                .collect(Collectors.toList());
	    }

	    //  Gestion des départements 

	    @Override
	    public void creerDepartement(DepartementRequest req) {

	        if (departementRepo.existsByCode(req.getCode())) {
	            throw new BusinessException("Un département avec ce code existe déjà : "
	                    + req.getCode());
	        }

	        Departement dept = new Departement();
	        dept.setNom(req.getNom());
	        dept.setCode(req.getCode());
	        departementRepo.save(dept);
	    }

	    @Override
	    public void modifierDepartement(Long deptId, DepartementRequest req) {

	        Departement dept = departementRepo.findById(deptId)
	                .orElseThrow(() -> new ResourceNotFoundException("Département", deptId));

	        if (req.getNom()  != null) dept.setNom(req.getNom());
	        if (req.getCode() != null) dept.setCode(req.getCode());
	        departementRepo.save(dept);
	    }

	    @Override
	    public void supprimerDepartement(Long deptId) {

	        Departement dept = departementRepo.findById(deptId)
	                .orElseThrow(() -> new ResourceNotFoundException("Département", deptId));

	        // Vérification : pas d'étudiants ou encadrants encore rattachés
	        if (!dept.getEtudiants().isEmpty() || !dept.getEncadrants().isEmpty()) {
	            throw new BusinessException(
	                    "Impossible de supprimer un département non vide");
	        }

	        departementRepo.delete(dept);
	    }

	    @Override
	    public List<DepartementResponse> getTousDepartements() {
	        return departementRepo.findAll()
	                .stream()
	                .map(DepartementResponse::fromEntity)
	                .collect(Collectors.toList());
	    }

	    @Override
	    public void assignerChef(Long deptId, Long userId) {

	        Departement dept = departementRepo.findById(deptId)
	                .orElseThrow(() -> new ResourceNotFoundException("Département", deptId));

	        Utilisateur user = utilisateurRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));

	        if (!(user instanceof ChefDepartement chef)) {
	            throw new BusinessException(
	                    "Cet utilisateur n'a pas le rôle CHEF_DEPARTEMENT");
	        }

	        chef.setDepartement(dept);
	        utilisateurRepo.save(chef);

	        logAuditService.log(user, "ASSIGNATION_CHEF", null, "SUCCES",
	                "Département : " + dept.getNom());
	    }

	    //  Affectations globales 
	    @Override
	    public void forcerAffectationGlobale(Long etudiantId, Long encadrantId) {

	        Etudiant etudiant = etudiantRepo.findById(etudiantId)
	                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", etudiantId));

	        Encadrant encadrant = encadrantRepo.findById(encadrantId)
	                .orElseThrow(() -> new ResourceNotFoundException("Encadrant", encadrantId));

	        etudiant.setEncadrant(encadrant);
	        etudiant.setAffectationForcee(true);
	        etudiantRepo.save(etudiant);

	        logAuditService.log(etudiant, "AFFECTATION_FORCEE_GLOBALE", null, "SUCCES",
	                "Encadrant : " + encadrant.getNomComplet());
	    }

	    @Override
	    public void rompreAffectation(Long etudiantId) {

	        Etudiant etudiant = etudiantRepo.findById(etudiantId)
	                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", etudiantId));

	        Encadrant ancien = etudiant.getEncadrant();
	        etudiant.setEncadrant(null);
	        etudiant.setAffectationForcee(false);
	        etudiantRepo.save(etudiant);

	        logAuditService.log(etudiant, "RUPTURE_AFFECTATION", null, "SUCCES",
	                ancien != null ? "Ancien encadrant : " + ancien.getNomComplet() : null);
	    }

	    @Override
	    public void transfererEtudiant(Long etudiantId, Long nouveauDeptId) {

	        Etudiant etudiant = etudiantRepo.findById(etudiantId)
	                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", etudiantId));

	        Departement dept = departementRepo.findById(nouveauDeptId)
	                .orElseThrow(() -> new ResourceNotFoundException("Département", nouveauDeptId));

	        // Rupture affectation lors d'un transfert inter-département
	        etudiant.setEncadrant(null);
	        etudiant.setDepartement(dept);
	        etudiantRepo.save(etudiant);

	        logAuditService.log(etudiant, "TRANSFERT_DEPARTEMENT", null, "SUCCES",
	                "Nouveau département : " + dept.getNom());
	    }

	    // Supervision globale

	    @Override
	    public DashboardGlobalResponse getDashboardComplet() {

	        long totalEtudiants  = etudiantRepo.count();
	        long totalEncadrants = encadrantRepo.count();
	        long nonAffectes     = etudiantRepo.countByEncadrantIsNull();
	        long verrouillesCount = utilisateurRepo.countByAccountLockedTrue();

	        return DashboardGlobalResponse.builder()
	                .totalEtudiants(totalEtudiants)
	                .totalEncadrants(totalEncadrants)
	                .totalDepartements(departementRepo.count())
	                .etudiantsNonAffectes(nonAffectes)
	                .etudiantsAffectes(totalEtudiants - nonAffectes)
	                .comptesVerrouilles(verrouillesCount)
	                .build();
	    }

	    @Override
	    public List<LogAuditResponse> getTousLesLogs() {
	        return logAuditRepo.findAll()
	                .stream()
	                .map(LogAuditResponse::fromEntity)
	                .collect(Collectors.toList());
	    }

	    @Override
	    public void reinitialiserSysteme() {
	        // Révocation de tous les refresh tokens actifs
	        refreshTokenRepo.supprimerTokensInvalides();
	        logAuditService.log(null, "REINITIALISATION_SYSTEME", null, "SUCCES",
	                "Tous les tokens révoqués");
	    }



	    private String genererMotDePasse() {
	        String chars =
	            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
	        StringBuilder sb = new StringBuilder();
	        java.security.SecureRandom rnd = new java.security.SecureRandom();
	        for (int i = 0; i < 12; i++) {
	            sb.append(chars.charAt(rnd.nextInt(chars.length())));
	        }
	        return sb.toString();
	    }

	    private Utilisateur creerUtilisateurSelon(CreerCompteRequest req, String mdpTemp) {

	        Utilisateur user = switch (req.getRole()) {
	            case ROLE_ETUDIANT        -> new Etudiant();
	            case ROLE_ENCADRANT       -> new Encadrant();
	            case ROLE_CHEF_DEPARTEMENT -> new ChefDepartement();
	            case ROLE_DIRECTEUR       -> new DirecteurDesStages();
	            case ROLE_SERVICE_STAGE   -> new ServiceDesStages();
	            case ROLE_ADMIN           -> new Admin();
	        };

	        user.setEmail(req.getEmail());
	        user.setNomComplet(req.getNomComplet());
	        user.setMotDePasse(passwordEncoder.encode(mdpTemp));
	        user.setRole(req.getRole());
	        user.setMustChangePassword(true);
	        // L'admin crée les comptes directement activés
	        user.setEnabled(true);
	        return user;
	    }
}
