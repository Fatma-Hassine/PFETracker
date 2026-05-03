// EtudiantStageResponse.java
package com.pfetracker.dto.module1;

import com.pfetracker.entity.module1.Etudiant;
import lombok.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantStageResponse {

    private Long id;
    private String nomComplet;
    private String email;
    private String departementNom;
    private String encadrantNom;       
    private LocalDate dateDebutStage;
    private LocalDate dateFinStage;
    private boolean affecte;
    private boolean affectationForcee;

    private long joursDepuisDebut;

    private boolean enAlerte;

    public static EtudiantStageResponse fromEntity(Etudiant e) {

        long joursDepuis = 0;
        if (e.getDateDebutStage() != null) {
            joursDepuis = ChronoUnit.DAYS.between(
                    e.getDateDebutStage(), LocalDate.now());
        }

        return EtudiantStageResponse.builder()
                .id(e.getId())
                .nomComplet(e.getNomComplet())
                .email(e.getEmail())
                .departementNom(e.getDepartement() != null
                        ? e.getDepartement().getNom() : null)
                .encadrantNom(e.getEncadrant() != null
                        ? e.getEncadrant().getNomComplet() : null)
                .dateDebutStage(e.getDateDebutStage())
                .dateFinStage(e.getDateFinStage())
                .affecte(e.getEncadrant() != null)
                .affectationForcee(e.isAffectationForcee())
                .joursDepuisDebut(joursDepuis)
                .enAlerte(e.getEncadrant() == null && joursDepuis > 21)
                .build();
    }
}