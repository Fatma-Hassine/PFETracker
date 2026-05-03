package com.pfetracker.entity.module1;

import java.util.UUID;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;
import lombok.EqualsAndHashCode;
@Entity
@Table(name = "etudiants")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@DiscriminatorValue("ETUDIANT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant extends Utilisateur{
	private String grade;
    private String specialite;
    private String bureau;

    private String telephone;
    private String niveauEtudes;
    private String annee;
    
    private LocalDate dateDebutStage;
    private LocalDate dateFinStage;
    private boolean affectationForcee = false;
  
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
    @ManyToOne
    @JoinColumn(name = "encadrant_id")
    private Encadrant encadrant;
}
