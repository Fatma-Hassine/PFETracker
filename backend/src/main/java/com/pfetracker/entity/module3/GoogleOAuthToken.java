package com.pfetracker.entity.module3;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Jeton OAuth2 Google Calendar d'un utilisateur (cahier des charges §6.3.1 :
 * génération automatique d'un lien Google Meet via l'API Google Calendar).
 */
@Entity
@Table(name = "google_oauth_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleOAuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String userEmail;

    @Column(nullable = false, length = 500)
    private String refreshToken;

    @Column(length = 2000)
    private String accessToken;

    private LocalDateTime accessTokenExpiration;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
