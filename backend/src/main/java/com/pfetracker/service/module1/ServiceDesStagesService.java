package com.pfetracker.service.module1;

import com.pfetracker.dto.module1.*;
import java.util.List;

import org.springframework.stereotype.Service;
@Service("serviceDesStagesServiceM1")

public interface ServiceDesStagesService {


    List<EtudiantStageResponse> getTousLesEtudiants();
    List<EtudiantStageResponse> getStagesEnCours();
    List<EtudiantStageResponse> getStagesProchesExpiration();
    List<EtudiantStageResponse> getStagesExpires();


    ServiceStagesDashboardResponse getDashboard();


   
}