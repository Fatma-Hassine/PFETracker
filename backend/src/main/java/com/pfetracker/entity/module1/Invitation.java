package com.pfetracker.entity.module1;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.pfetracker.entity.module1.enums.StatutInvitation;

@Entity
@Table(name = "invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, unique = true)
	    private String token;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private StatutInvitation statut = StatutInvitation.EN_ATTENTE;

	    @Column(nullable = false)
	    private LocalDateTime dateExpiration;

	 
	   @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "encadrant_id", nullable = false)
	    private Encadrant encadrant;

	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "etudiant_id", nullable = false)
	    private Etudiant etudiant;

	    public boolean estExpiree() {
	        return LocalDateTime.now().isAfter(dateExpiration);
	    }
}
