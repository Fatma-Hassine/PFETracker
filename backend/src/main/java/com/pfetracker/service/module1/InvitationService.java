package com.pfetracker.service.module1;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pfetracker.dto.module1.InvitationResponse;
@Service("invitationServiceM1")

public interface InvitationService {
	void envoyerInvitation(Long encadrantId, Long etudiantId);
    void accepterInvitation(String token);
    void refuserInvitation(String token);
    List<InvitationResponse> getHistoriqueInvitations(Long encadrantId);

}
