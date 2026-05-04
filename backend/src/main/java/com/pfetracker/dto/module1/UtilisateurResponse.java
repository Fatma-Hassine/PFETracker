package com.pfetracker.dto.module1;
import com.pfetracker.entity.*;
import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.entity.module1.enums.Role;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponse {
	private Long id;
    private String email;
    private String nomComplet;
    private Role role;
    private boolean enabled;
    private boolean accountLocked;

    public static UtilisateurResponse fromEntity(Utilisateur u) {
        return UtilisateurResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .nomComplet(u.getNomComplet())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .accountLocked(u.isAccountLocked())
                .build();
    }
}
