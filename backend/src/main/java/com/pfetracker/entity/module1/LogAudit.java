package com.pfetracker.entity.module1;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String typeAction; 
    
    @Column(nullable = false)
    private LocalDateTime horodatage = LocalDateTime.now();

    @Column(nullable = false)
    private String adresseIp;

    @Column(nullable = false)
    private String resultat; 

    private String details; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
