package com.pfetracker.dto.module1;

import com.pfetracker.entity.module1.ChefDepartement;
import com.pfetracker.entity.module1.Encadrant;
import com.pfetracker.entity.module1.Etudiant;
import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.entity.module1.enums.Role;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponse {
	private Long id;
    private String email;
    private String nomComplet;
    private Role role;
    private boolean enabled;
    private boolean accountLocked;

    // Spécifique Étudiant (§4.2.1)
    private String telephone;
    private String niveauEtudes;
    private String annee;
    private Long encadrantId;
    private String encadrantNom;

    // Spécifique Encadrant (§4.2.2)
    private String grade;
    private String specialite;
    private String bureau;
    private String codeId;

    // Commun (§4.2.1/4.2.2 + chef de département)
    private Long departementId;
    private String departementNom;

    public static UtilisateurResponse fromEntity(Utilisateur u) {
        UtilisateurResponseBuilder builder = UtilisateurResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .nomComplet(u.getNomComplet())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .accountLocked(u.isAccountLocked());

        if (u instanceof Etudiant e) {
            builder.telephone(e.getTelephone())
                    .niveauEtudes(e.getNiveauEtudes())
                    .annee(e.getAnnee())
                    .departementId(e.getDepartement() != null ? e.getDepartement().getId() : null)
                    .departementNom(e.getDepartement() != null ? e.getDepartement().getNom() : null)
                    .encadrantId(e.getEncadrant() != null ? e.getEncadrant().getId() : null)
                    .encadrantNom(e.getEncadrant() != null ? e.getEncadrant().getNomComplet() : null);
        } else if (u instanceof Encadrant enc) {
            builder.grade(enc.getGrade())
                    .specialite(enc.getSpecialite())
                    .bureau(enc.getBureau())
                    .codeId(enc.getCodeId())
                    .departementId(enc.getDepartement() != null ? enc.getDepartement().getId() : null)
                    .departementNom(enc.getDepartement() != null ? enc.getDepartement().getNom() : null);
        } else if (u instanceof ChefDepartement chef) {
            builder.departementId(chef.getDepartement() != null ? chef.getDepartement().getId() : null)
                    .departementNom(chef.getDepartement() != null ? chef.getDepartement().getNom() : null);
        }

        return builder.build();
    }
}
