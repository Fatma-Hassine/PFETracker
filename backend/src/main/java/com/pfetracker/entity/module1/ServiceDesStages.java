package com.pfetracker.entity.module1;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "services_stages")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@DiscriminatorValue("SERVICE_STAGE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDesStages extends Utilisateur{
	private String bureau;

    private String telephoneService;

    private boolean gestionDocuments = true;
}
