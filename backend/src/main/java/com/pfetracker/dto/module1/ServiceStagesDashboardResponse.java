package com.pfetracker.dto.module1;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStagesDashboardResponse {

    private long totalEtudiants;
    private long stagesEnCours;
    private long stagesProchesExpiration;
    private long stagesExpires;
}