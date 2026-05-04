package com.pfetracker.dto.module1;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardGlobalResponse {
    private long totalEtudiants;
    private long totalEncadrants;
    private long totalDepartements;

    private long etudiantsNonAffectes;
    private long etudiantsAffectes;
    private long affectationsForcees;

    private long stagesEnCours;
    private long stagesTermines;
    private long stagesEnRetard;
    private long stagesProchesExpiration;

    private long pfeActifs;
    private long pfeTermines;
    private long pfeEnRetard;
    private long pfeNonDemarres;

    private double progressionMoyenneGlobale;

    private long comptesVerrouilles;
    private long pfesStagnants;
    private long notificationsNonLues;
}
