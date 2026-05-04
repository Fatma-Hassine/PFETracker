package com.pfetracker.dto.module1;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStagesDashboardResponse {

    private long totalConventions;
    private long conventionsValidees;
    private long conventionsEnAttente;
    private long soutenancesPlanifiees;
    private long dossiersincomplets;
}