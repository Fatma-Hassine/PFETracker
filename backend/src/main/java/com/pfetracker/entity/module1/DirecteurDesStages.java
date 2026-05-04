package com.pfetracker.entity.module1;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "directeurs_stages")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@DiscriminatorValue("DIRECTEUR")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DirecteurDesStages extends Utilisateur{
	@Column(name = "autorisation_affectation_automatique", nullable = false)
	private boolean autorisationAffectationAutomatique = true;

	@Column(name = "delai_max_affectation_jours", nullable = false)
	private Integer delaiMaxAffectationJours = 21;
}
