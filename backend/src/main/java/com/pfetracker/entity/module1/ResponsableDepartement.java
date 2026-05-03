package com.pfetracker.entity.module1;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "responsables_departement")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@DiscriminatorValue("RESPONSABLE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResponsableDepartement extends Utilisateur{
	private Integer limiteEtudiantsParEncadrant = 10;

    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id", unique = true)
    private Departement departement;
}
