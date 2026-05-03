package com.pfetracker.dto.module1;
import com.pfetracker.entity.module1.Departement;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartementResponse {
	 private Long id;
	    private String nom;
	    private String code;
	    private String chefNom;
	    private int nombreEtudiants;
	    private int nombreEncadrants;

	    public static DepartementResponse fromEntity(Departement d) {
	        return DepartementResponse.builder()
	                .id(d.getId())
	                .nom(d.getNom())
	                .code(d.getCode())
	                .chefNom(d.getChef() != null
	                        ? d.getChef().getNomComplet() : null)
	                .nombreEtudiants(d.getEtudiants() != null
	                        ? d.getEtudiants().size() : 0)
	                .nombreEncadrants(d.getEncadrants() != null
	                        ? d.getEncadrants().size() : 0)
	                .build();
	    }
}
