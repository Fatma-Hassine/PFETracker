package com.pfetracker.mapper.module1;

import com.pfetracker.dto.module1.InvitationResponse;
import com.pfetracker.entity.module1.Invitation;
import org.springframework.stereotype.Component;

@Component("invitationMapperM1")

public class InvitationMapper {
	 public InvitationResponse toResponse(Invitation inv) {
	        return InvitationResponse.builder()
	                .id(inv.getId())
	                .etudiantNom(inv.getEtudiant().getNomComplet())
	                .etudiantEmail(inv.getEtudiant().getEmail())
	                .statut(inv.getStatut())
	                .dateExpiration(inv.getDateExpiration())
	                .build();
	    }
}
