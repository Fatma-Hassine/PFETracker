package com.pfetracker.entity.module1;

import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "chefs_departement")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@DiscriminatorValue("CHEF_DEPARTEMENT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChefDepartement extends Utilisateur{
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id", unique = true)
    private Departement departement;

    private Integer limiteEtudiantsParEncadrant = 10;
}
