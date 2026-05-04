package com.pfetracker.service.module1;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pfetracker.dto.module1.DashboardGlobalResponse;
import com.pfetracker.dto.module1.DirecteurDashboardResponse;
import com.pfetracker.dto.module1.EtudiantResponse;
import com.pfetracker.dto.module1.EtudiantStageResponse;
@Service("directeurServiceM1")

public interface DirecteurService {
	List<EtudiantStageResponse> getTousLesEtudiants();
    List<EtudiantStageResponse> getEtudiantsNonAffectes();
    List<EtudiantStageResponse> getEtudiantsEnAlerte();         
    List<EtudiantStageResponse> getStagesProchesExpiration();
    List<EtudiantStageResponse> getStagesExpires();

    
     
    DashboardGlobalResponse getDashboardGlobal();

    void verifierEtAffecterEtudiantsNonAffectes();

    void affecterManuellement(Long etudiantId, Long encadrantId);

    void reaffecterEtudiant(Long etudiantId, Long nouvelEncadrantId);

    List<EtudiantResponse> getEtudiantsEnRetard();
    DirecteurDashboardResponse getDashboard(); 
    void verifierEtAffecterApresDelai();                
    void reaffecter(Long etudiantId, Long nouvelEncadrantId);
}
