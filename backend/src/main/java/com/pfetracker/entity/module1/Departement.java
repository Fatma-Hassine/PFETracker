package com.pfetracker.entity.module1;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "departements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(unique = true)
    private String code;
    

   
    @OneToMany(mappedBy = "departement")
    private List<Etudiant> etudiants;

    
    @OneToMany(mappedBy = "departement")
    private List<Encadrant> encadrants;

    @OneToOne(mappedBy = "departement")
    private ResponsableDepartement responsable;
   
    @OneToOne(mappedBy = "departement")
    private ChefDepartement chef;

   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_superviseur_id")
    private Admin adminSuperviseur;
}
