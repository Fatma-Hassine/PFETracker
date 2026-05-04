package com.pfetracker.entity.module1;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "encadrants")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@DiscriminatorValue("ENCADRANT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Encadrant extends Utilisateur{
	private String grade;
    private String specialite;
    private String bureau;
    
   
    @Column(unique = true, nullable = false)
    private String codeId = "ENC-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

    
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @OneToMany(mappedBy = "encadrant")
    private List<Etudiant> etudiants;

    
    @OneToMany(mappedBy = "encadrant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> invitations;

    public void regenererCode() {
        this.codeId = "ENC-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }
}
