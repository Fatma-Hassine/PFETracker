package com.pfetracker.entity.module1;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token; // UUID, usage unique

    @Column(nullable = false)
    private LocalDateTime expiration; // expire dans 1h

    private boolean used = false;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration);
    }
    
}
