package com.pfetracker.entity.module1;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@DiscriminatorValue("ADMIN")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends Utilisateur{

    private boolean droitReinitialisation = true;

    private boolean gestionTousDepartements = true;

    private boolean affectationForceeGlobale = true;


    
    @OneToMany(mappedBy = "adminSuperviseur", fetch = FetchType.LAZY)
    private List<Departement> departements;

    
    public boolean aTousLesDroits() {
        return droitReinitialisation
                && gestionTousDepartements
                && affectationForceeGlobale;
    }
}

