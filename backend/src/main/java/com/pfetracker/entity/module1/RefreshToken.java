package com.pfetracker.entity.module1;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, unique = true)
	    private String token;

	    @Column(nullable = false)
	    private Instant expirationDate; 

	    private boolean revoked = false;
	    
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "utilisateur_id", nullable = false)
	    private Utilisateur utilisateur;

	    public boolean isExpired() {
	        return Instant.now().isAfter(expirationDate);
	    }
	    
}
