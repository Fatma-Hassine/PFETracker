package com.pfetracker.service.impl.module1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pfetracker.dto.module1.*;
import com.pfetracker.entity.module1.*;
import com.pfetracker.repository.module1.*;
import com.pfetracker.service.module1.LogAuditService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LogAuditServiceImpl implements LogAuditService{
	private final LogAuditRepository logAuditRepo;
    private final EtudiantRepository etudiantRepo;
    private final EncadrantRepository encadrantRepo;

    @Override
    public void log(Utilisateur utilisateur, String typeAction,
                    String adresseIp, String resultat, String details) {
        LogAudit log = new LogAudit();
        log.setUtilisateur(utilisateur);
        log.setTypeAction(typeAction);
        log.setAdresseIp(adresseIp);
        log.setResultat(resultat);
        log.setDetails(details); 
        logAuditRepo.save(log);
    }

    @Override
    public List<LogAuditResponse> getLogsDepartement(Long departementId) {
        // Récupère les logs de tous les utilisateurs du département
        List<Long> etudiantIds = etudiantRepo.findByDepartementId(departementId)
                .stream().map(Utilisateur::getId).collect(Collectors.toList());
        List<Long> encadrantIds = encadrantRepo.findByDepartementId(departementId)
                .stream().map(Utilisateur::getId).collect(Collectors.toList());

        etudiantIds.addAll(encadrantIds);

        return etudiantIds.stream()
                .flatMap(id -> logAuditRepo
                        .findByUtilisateurIdOrderByHorodatageDesc(id).stream())
                .map(LogAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<LogAuditResponse> getLogsUtilisateur(Long userId) {
        return logAuditRepo.findByUtilisateurIdOrderByHorodatageDesc(userId)
                .stream()
                .map(LogAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
