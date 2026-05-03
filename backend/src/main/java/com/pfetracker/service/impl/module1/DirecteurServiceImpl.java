package com.pfetracker.service.impl.module1;

import com.pfetracker.dto.module1.*;
import com.pfetracker.entity.module1.*;
import com.pfetracker.exception.module1.BusinessException;
import com.pfetracker.repository.module1.*;
import com.pfetracker.service.module1.DirecteurService;
import com.pfetracker.service.module1.LogAuditService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service("directeurServiceM1")
@RequiredArgsConstructor
@Transactional
public class DirecteurServiceImpl implements DirecteurService {

    private final EtudiantRepository etudiantRepo;
    private final EncadrantRepository encadrantRepo;
    private final LogAuditService logAuditService;


    @Override
    public List<EtudiantStageResponse> getTousLesEtudiants() {
        return etudiantRepo.findAll()
                .stream()
                .map(EtudiantStageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<EtudiantStageResponse> getEtudiantsNonAffectes() {
        return etudiantRepo.findByEncadrantIsNull()
                .stream()
                .map(EtudiantStageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<EtudiantStageResponse> getEtudiantsEnAlerte() {
        LocalDate dateLimite = LocalDate.now().minusDays(21);
        return etudiantRepo.findNonAffectesDepuis(dateLimite)
                .stream()
                .map(EtudiantStageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<EtudiantStageResponse> getStagesProchesExpiration() {
        LocalDate dateLimite = LocalDate.now().plusWeeks(2);
        return etudiantRepo.findStagesProchesExpiration(dateLimite)
                .stream()
                .map(EtudiantStageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<EtudiantStageResponse> getStagesExpires() {
        return etudiantRepo.findStagesExpires()
                .stream()
                .map(EtudiantStageResponse::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    public List<EtudiantResponse> getEtudiantsEnRetard() {
        return etudiantRepo.findStagesExpires()
                .stream()
                .map(EtudiantResponse::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    public DirecteurDashboardResponse getDashboard() {
        long total       = etudiantRepo.count();
        long nonAffectes = etudiantRepo.countByEncadrantIsNull();
        long affectes    = etudiantRepo.countByEncadrantIsNotNull();

        LocalDate dateLimite21 = LocalDate.now().minusDays(21);
        long enAlerte = etudiantRepo.findNonAffectesDepuis(dateLimite21).size();

        List<EtudiantStageResponse> prochesExpiration =
                getStagesProchesExpiration();

        return DirecteurDashboardResponse.builder()
                .totalEtudiants(total)
                .etudiantsAffectes(affectes)
                .etudiantsNonAffectes(nonAffectes)
                .etudiantsEnAlerte(enAlerte)
                .stagesProchesExpiration(prochesExpiration)
                .build();
    }

    @Override
    public DashboardGlobalResponse getDashboardGlobal() {
        long total       = etudiantRepo.count();
        long nonAffectes = etudiantRepo.countByEncadrantIsNull();
        long affectes    = etudiantRepo.countByEncadrantIsNotNull();

        return DashboardGlobalResponse.builder()
                .totalEtudiants(total)
                .etudiantsNonAffectes(nonAffectes)
                .etudiantsAffectes(affectes)
                .build();
    }


    @Override
    public void verifierEtAffecterApresDelai() {
        LocalDate dateLimite = LocalDate.now().minusDays(21);
        List<Etudiant> enAlerte = etudiantRepo.findNonAffectesDepuis(dateLimite);
        for (Etudiant etudiant : enAlerte) {
            affecterAutomatiquement(etudiant);
        }
    }

    @Override
    public void verifierEtAffecterEtudiantsNonAffectes() {
        verifierEtAffecterApresDelai();
    }

    @Override
    public void affecterManuellement(Long etudiantId, Long encadrantId) {
        Etudiant etudiant = etudiantRepo.findById(etudiantId)
                .orElseThrow(() -> new BusinessException("Étudiant introuvable"));
        Encadrant encadrant = encadrantRepo.findById(encadrantId)
                .orElseThrow(() -> new BusinessException("Encadrant introuvable"));

        etudiant.setEncadrant(encadrant);
        etudiant.setAffectationForcee(true);
        etudiantRepo.save(etudiant);

        logAuditService.log(etudiant, "AFFECTATION_MANUELLE_DIRECTEUR",
                null, "SUCCES", "Encadrant : " + encadrant.getNomComplet());
    }

    @Override
    public void reaffecter(Long etudiantId, Long nouvelEncadrantId) {
        affecterManuellement(etudiantId, nouvelEncadrantId);
    }

    @Override
    public void reaffecterEtudiant(Long etudiantId, Long nouvelEncadrantId) {
        affecterManuellement(etudiantId, nouvelEncadrantId);
    }


    private void affecterAutomatiquement(Etudiant etudiant) {
        List<Encadrant> encadrants = encadrantRepo.findByDepartementId(
                etudiant.getDepartement().getId());

        if (encadrants.isEmpty()) {
            throw new BusinessException(
                    "Aucun encadrant disponible dans le département : "
                    + etudiant.getDepartement().getNom());
        }

        Encadrant choisi = encadrants.stream()
                .min(Comparator.comparingLong(
                        enc -> etudiantRepo.countByEncadrantId(enc.getId())))
                .orElseThrow(() ->
                        new BusinessException("Aucun encadrant disponible"));

        etudiant.setEncadrant(choisi);
        etudiant.setAffectationForcee(true);
        etudiantRepo.save(etudiant);

        logAuditService.log(etudiant, "AFFECTATION_AUTOMATIQUE_DIRECTEUR",
                null, "SUCCES",
                "Encadrant choisi : " + choisi.getNomComplet());
    }
}