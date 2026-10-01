package com.pfetracker.mapper.module1;
import com.pfetracker.dto.module1.ProfilUpdateRequest;
import com.pfetracker.dto.module1.UtilisateurResponse;

import com.pfetracker.entity.module1.Utilisateur;

import org.springframework.stereotype.Component;

@Component("utilisateurMapperM1")

public class UtilisateurMapper {
	 public UtilisateurResponse toResponse(Utilisateur u) {
	        return UtilisateurResponse.builder()
	                .id(u.getId())
	                .email(u.getEmail())
	                .nomComplet(u.getNomComplet())
	                .role(u.getRole())
	                .enabled(u.isEnabled())
	                .accountLocked(u.isAccountLocked())
	                .build();
	    }

	    public void updateFromRequest(ProfilUpdateRequest req, Utilisateur u) {
	        if (req.getNomComplet() != null) u.setNomComplet(req.getNomComplet());
	    }
}
