package com.pfetracker.dto.module1;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirecteurDashboardResponse {

    private long totalEtudiants;
    private long etudiantsAffectes;
    private long etudiantsNonAffectes;

    private long etudiantsEnAlerte;

    private List<EtudiantStageResponse> stagesProchesExpiration;
}