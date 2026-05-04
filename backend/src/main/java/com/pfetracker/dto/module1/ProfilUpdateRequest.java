package com.pfetracker.dto.module1;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfilUpdateRequest {
	  // Email et département non modifiables ici (admin uniquement)
    private String nomComplet;
    private String photoProfil;
    private String telephone;     // spécifique Etudiant
    private String grade;         // spécifique Encadrant
    private String specialite;    // spécifique Encadrant
    private String bureau;
}
