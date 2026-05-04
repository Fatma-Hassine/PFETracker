package com.pfetracker.service.module1;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pfetracker.dto.module1.DashboardDepartementResponse;
import com.pfetracker.dto.module1.EncadrantChargeResponse;
import com.pfetracker.dto.module1.EtudiantResponse;
import com.pfetracker.dto.module1.UtilisateurResponse;
@Service("responsableServiceM1")

public interface ResponsableService {
    List<UtilisateurResponse> getComptesEnAttente(Long departementId);
    void validerCompte(Long userId);
    void refuserCompte(Long userId, String motif);
    void activerDesactiverCompte(Long userId, boolean activer);
    void deverrouillerCompte(Long userId);
    void reinitialiserMotDePasseForce(Long userId);
    void changerDepartement(Long userId, Long nouveauDepartementId);

    List<EtudiantResponse> getEtudiantsSansEncadrant(Long departementId);
    void forcerAffectation(Long etudiantId, Long encadrantId);
    void rompreAffectation(Long etudiantId);
    List<EncadrantChargeResponse> getChargeEncadrants(Long departementId);
    void configurerLimiteEtudiants(Long departementId, int limite);

    DashboardDepartementResponse getDashboard(Long departementId);
    void regenererCodeEncadrant(Long encadrantId);
   
}
