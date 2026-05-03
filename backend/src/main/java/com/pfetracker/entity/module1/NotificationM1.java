package com.pfetracker.entity.module1;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.pfetracker.entity.module1.enums.TypeNotification;

@Entity(name = "Module1Notification")
@Table(name = "module1_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationM1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNotification type;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Boolean lu = false;
    private LocalDateTime dateCreation = LocalDateTime.now();
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    public void marquerLu() { this.lu = true; }
}
