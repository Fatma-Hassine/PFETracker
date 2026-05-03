package com.pfetracker.service.impl.module1;

import com.pfetracker.dto.module1.*;
import com.pfetracker.repository.module1.EtudiantRepository;
import com.pfetracker.service.module1.ServiceDesStagesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceDesStagesServiceImpl implements ServiceDesStagesService {

    private final EtudiantRepository etudiantRepo;

    @Override
    public List<EtudiantStageResponse> getTousLesEtudiants() {
        return etudiantRepo.findAll()
                .stream()
                .map(EtudiantStageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<EtudiantStageResponse> getStagesEnCours() {
        return etudiantRepo.findAll()
                .stream()
                .filter(e -> e.getDateDebutStage() != null
                        && !LocalDate.now().isBefore(e.getDateDebutStage())
                        && (e.getDateFinStage() == null
                            || !LocalDate.now().isAfter(e.getDateFinStage())))
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
    public ServiceStagesDashboardResponse getDashboard() {
        return ServiceStagesDashboardResponse.builder()
                .totalConventions(etudiantRepo.count())
                .conventionsValidees(etudiantRepo.countByEncadrantIsNotNull())
                .conventionsEnAttente(etudiantRepo.countByEncadrantIsNull())
                .soutenancesPlanifiees(0L)
                .dossiersincomplets(0L)
                .build();
    }

  
}