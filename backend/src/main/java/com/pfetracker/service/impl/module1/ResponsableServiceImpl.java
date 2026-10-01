package com.pfetracker.service.impl.module1;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pfetracker.repository.module1.*;
import com.pfetracker.service.module1.*;
import com.pfetracker.dto.module1.*;
import com.pfetracker.entity.module1.*;
import com.pfetracker.entity.module1.enums.TypeNotification;
import com.pfetracker.exception.module1.BusinessException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ResponsableServiceImpl implements ResponsableService {

    private final UtilisateurRepository utilisateurRepo;
    private final EtudiantRepository etudiantRepo;
    private final EncadrantRepository encadrantRepo;
    private final DepartementRepository departementRepo;
    // MODIF : ResponsableDepartementRepository (table "responsables_departement")
    // n'est jamais peuplée — AdminServiceImpl crée les chefs de département en
    // tant que ChefDepartement (table "chefs_departement"), ce qui faisait
    // échouer forcerAffectation()/configurerLimiteEtudiants() avec "Responsable
    // introuvable" pour tout le monde. On utilise le repository réellement
    // alimenté par la création de compte.
    private final ChefDepartementRepository responsableRepo;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final LogAuditService logAuditService;


    @Override
    public List<UtilisateurResponse> getComptesEnAttente(Long departementId) {
        // Étudiants ET encadrants : les deux rôles doivent être validés par le
        // chef de département avant de pouvoir accéder à l'application
        // (confidentialité — aucune création de compte automatique).
        List<UtilisateurResponse> enAttente = new java.util.ArrayList<>();

        encadrantRepo.findByDepartementIdAndEnabledFalse(departementId)
                .stream()
                .map(UtilisateurResponse::fromEntity)
                .forEach(enAttente::add);

        etudiantRepo.findByDepartementIdAndEnabledFalse(departementId)
                .stream()
                .map(UtilisateurResponse::fromEntity)
                .forEach(enAttente::add);

        return enAttente;
    }

    @Override
    public List<UtilisateurResponse> getComptesDepartement(Long departementId) {
        // §4.3.1 : consultation de la liste complète des comptes du département
        List<UtilisateurResponse> comptes = new java.util.ArrayList<>();

        encadrantRepo.findByDepartementId(departementId)
                .stream()
                .map(UtilisateurResponse::fromEntity)
                .forEach(comptes::add);

        etudiantRepo.findByDepartementId(departementId)
                .stream()
                .map(UtilisateurResponse::fromEntity)
                .forEach(comptes::add);

        return comptes;
    }

    @Override
    public void validerCompte(Long userId) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        user.setEnabled(true);
        utilisateurRepo.save(user);

        notificationService.creerNotification(
                userId,
                TypeNotification.COMPTE_VALIDE,
                "Votre compte a été validé. Vous pouvez maintenant vous connecter."
        );

        logAuditService.log(user, "VALIDATION_COMPTE", null, "SUCCES", null);
    }

    @Override
    public void refuserCompte(Long userId, String motif) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        user.setEnabled(false);
        utilisateurRepo.save(user);

        notificationService.creerNotification(
                userId,
                TypeNotification.COMPTE_REFUSE,
                "Votre demande de compte a été refusée. Motif : " + motif
        );

        logAuditService.log(user, "REFUS_COMPTE", null, "SUCCES", "Motif : " + motif);
    }

    @Override
    public void activerDesactiverCompte(Long userId, boolean activer) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        user.setEnabled(activer);
        utilisateurRepo.save(user);

        logAuditService.log(
                user,
                activer ? "ACTIVATION_COMPTE" : "DESACTIVATION_COMPTE",
                null,
                "SUCCES",
                null
        );
    }

    @Override
    public void deverrouillerCompte(Long userId) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);

        utilisateurRepo.save(user);

        logAuditService.log(user, "DEVERROUILLAGE_COMPTE", null, "SUCCES", null);
    }

    @Override
    public void reinitialiserMotDePasseForce(Long userId) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        String nouveauMdp = UUID.randomUUID().toString().substring(0, 10) + "A1!";

        user.setMotDePasse(passwordEncoder.encode(nouveauMdp));
        user.setMustChangePassword(true);

        utilisateurRepo.save(user);

        try {
            notificationService.envoyerEmailBienvenue(user.getEmail(), nouveauMdp);
        } catch (Exception e) {
            System.err.println("⚠️ Email de réinitialisation non envoyé pour "
                    + user.getEmail() + " : " + e.getMessage());
        }

        logAuditService.log(user, "REINITIALISATION_MDP_FORCE", null, "SUCCES", null);
    }

    @Override
    public void changerDepartement(Long userId, Long nouveauDepartementId) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        Departement dept = departementRepo.findById(nouveauDepartementId)
                .orElseThrow(() -> new BusinessException("Département introuvable"));

        if (user instanceof Etudiant e) {
            e.setDepartement(dept);
        } else if (user instanceof Encadrant enc) {
            enc.setDepartement(dept);
        } else {
            throw new BusinessException("Ce type d'utilisateur ne peut pas changer de département");
        }

        utilisateurRepo.save(user);

        logAuditService.log(
                user,
                "CHANGEMENT_DEPARTEMENT",
                null,
                "SUCCES",
                "Nouveau département : " + dept.getNom()
        );
    }


    @Override
    public List<EtudiantResponse> getEtudiantsSansEncadrant(Long departementId) {
        return etudiantRepo.findByDepartementIdAndEncadrantIsNull(departementId)
                .stream()
                .map(EtudiantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void forcerAffectation(Long etudiantId, Long encadrantId) {
        Etudiant etudiant = etudiantRepo.findById(etudiantId)
                .orElseThrow(() -> new BusinessException("Étudiant introuvable"));

        Encadrant encadrant = encadrantRepo.findById(encadrantId)
                .orElseThrow(() -> new BusinessException("Encadrant introuvable"));

        affecter(etudiant, encadrant);

        logAuditService.log(
                etudiant,
                "AFFECTATION_FORCEE",
                null,
                "SUCCES",
                "Encadrant : " + encadrant.getNomComplet()
        );
    }

    /** Logique d'affectation partagée entre l'action manuelle et l'import de fichier. */
    private void affecter(Etudiant etudiant, Encadrant encadrant) {
        Departement dept = etudiant.getDepartement();
        if (dept == null) {
            throw new BusinessException(
                    "L'étudiant " + etudiant.getEmail() + " n'a pas de département");
        }

        ChefDepartement resp = responsableRepo.findByDepartementId(dept.getId())
                .orElseThrow(() -> new BusinessException("Responsable introuvable"));

        long chargeActuelle = etudiantRepo.countByEncadrantId(encadrant.getId());

        if (chargeActuelle >= resp.getLimiteEtudiantsParEncadrant()) {
            throw new BusinessException(
                    "L'encadrant " + encadrant.getEmail() + " a atteint sa limite d'étudiants ("
                            + resp.getLimiteEtudiantsParEncadrant() + ")"
            );
        }

        etudiant.setEncadrant(encadrant);
        etudiantRepo.save(etudiant);
    }

    @Override
    public AffectationImportResultDTO importerAffectations(Long departementId, MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            throw new BusinessException("Le fichier est vide");
        }

        String nom = fichier.getOriginalFilename();
        if (nom == null || !(nom.endsWith(".xlsx") || nom.endsWith(".csv"))) {
            throw new BusinessException("Format non supporté. Utilisez un fichier .xlsx ou .csv");
        }

        List<String> messages = new java.util.ArrayList<>();
        int totalRows = 0, createdCount = 0, skippedCount = 0, errorCount = 0;

        List<String[]> lignes;
        try {
            lignes = nom.endsWith(".csv")
                    ? lireCsv(fichier)
                    : lireExcel(fichier);
        } catch (Exception e) {
            throw new BusinessException("Erreur de lecture du fichier : " + e.getMessage());
        }

        for (String[] colonnes : lignes) {
            totalRows++;
            try {
                if (colonnes.length < 2 || colonnes[0].isBlank() || colonnes[1].isBlank()) {
                    throw new BusinessException("Il faut 2 colonnes : email étudiant, email/code encadrant");
                }
                String emailEtudiant = colonnes[0].trim();
                String refEncadrant = colonnes[1].trim();

                Etudiant etudiant = etudiantRepo.findAll().stream()
                        .filter(e -> e.getEmail().equalsIgnoreCase(emailEtudiant)
                                && e.getDepartement() != null
                                && e.getDepartement().getId().equals(departementId))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(
                                "Étudiant introuvable dans ce département : " + emailEtudiant));

                Encadrant encadrant = encadrantRepo.findByCodeId(refEncadrant)
                        .or(() -> encadrantRepo.findByDepartementId(departementId).stream()
                                .filter(e -> e.getEmail().equalsIgnoreCase(refEncadrant))
                                .findFirst())
                        .orElseThrow(() -> new BusinessException(
                                "Encadrant introuvable dans ce département : " + refEncadrant));

                if (etudiant.getEncadrant() != null) {
                    skippedCount++;
                    messages.add("Ligne " + totalRows + " : " + emailEtudiant
                            + " a déjà un encadrant, ignoré");
                    continue;
                }

                affecter(etudiant, encadrant);
                logAuditService.log(etudiant, "AFFECTATION_IMPORTEE", null, "SUCCES",
                        "Encadrant : " + encadrant.getEmail());
                createdCount++;
                messages.add("Ligne " + totalRows + " : " + emailEtudiant + " → " + encadrant.getEmail());
            } catch (Exception e) {
                errorCount++;
                messages.add("Ligne " + totalRows + " : erreur - " + e.getMessage());
            }
        }

        return new AffectationImportResultDTO(totalRows, createdCount, skippedCount, errorCount, messages);
    }

    private List<String[]> lireCsv(MultipartFile fichier) throws Exception {
        List<String[]> lignes = new java.util.ArrayList<>();
        try (var reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(fichier.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            String ligne;
            boolean premiere = true;
            while ((ligne = reader.readLine()) != null) {
                if (premiere) { premiere = false; continue; } // en-tête
                if (ligne.isBlank()) continue;
                lignes.add(ligne.split(";|,"));
            }
        }
        return lignes;
    }

    private List<String[]> lireExcel(MultipartFile fichier) throws Exception {
        List<String[]> lignes = new java.util.ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(fichier.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String v0 = valeurCellule(row.getCell(0));
                String v1 = valeurCellule(row.getCell(1));
                if (v0.isBlank() || v1.isBlank()) continue;
                lignes.add(new String[]{v0, v1});
            }
        }
        return lignes;
    }

    private String valeurCellule(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double v = cell.getNumericCellValue();
                yield v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
            }
            default -> cell.toString().trim();
        };
    }

    @Override
    public void rompreAffectation(Long etudiantId) {
        Etudiant etudiant = etudiantRepo.findById(etudiantId)
                .orElseThrow(() -> new BusinessException("Étudiant introuvable"));

        Encadrant ancien = etudiant.getEncadrant();

        etudiant.setEncadrant(null);
        etudiantRepo.save(etudiant);

        logAuditService.log(
                etudiant,
                "RUPTURE_AFFECTATION",
                null,
                "SUCCES",
                ancien != null ? "Ancien encadrant : " + ancien.getNomComplet() : null
        );
    }

    @Override
    public List<EncadrantChargeResponse> getChargeEncadrants(Long departementId) {
        return encadrantRepo.findByDepartementId(departementId)
                .stream()
                .map(enc -> new EncadrantChargeResponse(
                        enc.getId(),
                        enc.getNomComplet(),
                        (int) etudiantRepo.countByEncadrantId(enc.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void configurerLimiteEtudiants(Long departementId, int limite) {
        ChefDepartement resp = responsableRepo.findByDepartementId(departementId)
                .orElseThrow(() -> new BusinessException("Responsable introuvable"));

        resp.setLimiteEtudiantsParEncadrant(limite);
        responsableRepo.save(resp);
    }


    @Override
    public DashboardDepartementResponse getDashboard(Long departementId) {
        long totalActifs = etudiantRepo.countByDepartementId(departementId);
        long sansEncadrant = etudiantRepo.countByDepartementIdAndEncadrantIsNull(departementId);

        return DashboardDepartementResponse.builder()
                .totalEtudiants(totalActifs)
                .etudiantsSansEncadrant(sansEncadrant)
                .build();
    }

    @Override
    public void regenererCodeEncadrant(Long encadrantId) {
        Encadrant enc = encadrantRepo.findById(encadrantId)
                .orElseThrow(() -> new BusinessException("Encadrant introuvable"));

        enc.regenererCode();
        encadrantRepo.save(enc);

        logAuditService.log(enc, "REGENERATION_CODE_ENCADRANT", null, "SUCCES", null);
    }

}