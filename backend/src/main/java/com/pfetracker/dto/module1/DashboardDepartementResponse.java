package com.pfetracker.dto.module1;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDepartementResponse {
	private long totalEtudiants;
    private long etudiantsSansEncadrant;
    private long totalEncadrants;
    private int limiteEtudiantsParEncadrant;

    private long pfeActifs;
    private long pfeTermines;
    private long pfeEnRetard;
    private long pfeNonDemarres;
    private double progressionMoyenne;
    private long pfeInactifsSept;       
    private long pfeStagnants; 
}
