package com.pfetracker.dto.module1;
import lombok.*;
import java.time.LocalDateTime;

import com.pfetracker.entity.module1.Invitation;
import com.pfetracker.entity.module1.enums.StatutInvitation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse {
	private Long id;
    private String etudiantNom;
    private String etudiantEmail;
    private StatutInvitation statut;
    private LocalDateTime dateExpiration;

    public static InvitationResponse fromEntity(Invitation inv) {
        return InvitationResponse.builder()
                .id(inv.getId())
                .etudiantNom(inv.getEtudiant().getNomComplet())
                .etudiantEmail(inv.getEtudiant().getEmail())
                .statut(inv.getStatut())
                .dateExpiration(inv.getDateExpiration())
                .build();
    }
}
