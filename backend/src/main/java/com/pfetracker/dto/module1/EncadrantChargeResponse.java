package com.pfetracker.dto.module1;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncadrantChargeResponse {
	private Long encadrantId;
    private String nomComplet;
    private int nombreEtudiantsActuels;
    private int limiteMax;
    private boolean chargePleine;

    public EncadrantChargeResponse(Long id, String nom, int nbEtudiants) {
        this.encadrantId = id;
        this.nomComplet = nom;
        this.nombreEtudiantsActuels = nbEtudiants;
    }
}
