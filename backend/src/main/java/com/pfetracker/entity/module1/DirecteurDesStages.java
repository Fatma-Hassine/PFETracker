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
	private Integer delaiMaxAffectationJours = 21;

    private boolean autorisationAffectationAutomatique = true;

}
