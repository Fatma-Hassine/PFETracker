package com.pfetracker.dto.module1;
import com.pfetracker.entity.module1.Encadrant;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncadrantResponse {
	 private Long id;
	    private String nomComplet;
	    private String email;
	    private String grade;
	    private String specialite;
	    private String bureau;
	    private String codeId;          // code ENC-XXXXX affiché sur le profil
	    private String departementNom;
	    private boolean enabled;

	    public static EncadrantResponse fromEntity(Encadrant e) {
	        return EncadrantResponse.builder()
	                .id(e.getId())
	                .nomComplet(e.getNomComplet())
	                .email(e.getEmail())
	                .grade(e.getGrade())
	                .specialite(e.getSpecialite())
	                .bureau(e.getBureau())
	                .codeId(e.getCodeId())
	                .departementNom(e.getDepartement() != null ? e.getDepartement().getNom() : null)
	                .enabled(e.isEnabled())
	                .build();
	    }
}
