package com.pfetracker.dto.module1;
import com.pfetracker.entity.module1.Etudiant;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantResponse {
	private Long id;
    private String nomComplet;
    private String email;
    private String telephone;
    private String niveauEtudes;
    private String annee;
    private String departementNom;
    private String encadrantNom;    
    private boolean enabled;

    public static EtudiantResponse fromEntity(Etudiant e) {
        return EtudiantResponse.builder()
                .id(e.getId())
                .nomComplet(e.getNomComplet())
                .email(e.getEmail())
                .telephone(e.getTelephone())
                .niveauEtudes(e.getNiveauEtudes())
                .annee(e.getAnnee())
                .departementNom(e.getDepartement() != null ? e.getDepartement().getNom() : null)
                .encadrantNom(e.getEncadrant() != null ? e.getEncadrant().getNomComplet() : null)
                .enabled(e.isEnabled())
                .build();}
}
