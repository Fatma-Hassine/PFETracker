package com.pfetracker.controller.module1;
import com.pfetracker.dto.module1.*;
import com.pfetracker.service.module1.ServiceDesStagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("serviceDesStagesControllerM1")
@RequestMapping("/service-stages")
@PreAuthorize("hasRole('ROLE_SERVICE_STAGE')")
@RequiredArgsConstructor
public class ServiceDesStagesController {
	private final ServiceDesStagesService serviceDesStagesService;


    @GetMapping("/etudiants")
    public ResponseEntity<List<EtudiantStageResponse>> getTousLesEtudiants() {
        return ResponseEntity.ok(serviceDesStagesService.getTousLesEtudiants());
    }

    @GetMapping("/stages/en-cours")
    public ResponseEntity<List<EtudiantStageResponse>> getEnCours() {
        return ResponseEntity.ok(serviceDesStagesService.getStagesEnCours());
    }

    @GetMapping("/stages/proches-expiration")
    public ResponseEntity<List<EtudiantStageResponse>> getProchesExpiration() {
        return ResponseEntity.ok(
                serviceDesStagesService.getStagesProchesExpiration());
    }

    @GetMapping("/stages/expires")
    public ResponseEntity<List<EtudiantStageResponse>> getExpires() {
        return ResponseEntity.ok(serviceDesStagesService.getStagesExpires());
    }


    @GetMapping("/dashboard")
    public ResponseEntity<ServiceStagesDashboardResponse> getDashboard() {
        return ResponseEntity.ok(serviceDesStagesService.getDashboard());
    }


    
}
