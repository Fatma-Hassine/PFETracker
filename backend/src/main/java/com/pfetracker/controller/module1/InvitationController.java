package com.pfetracker.controller.module1;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pfetracker.dto.module1.InvitationResponse;
import com.pfetracker.service.module1.InvitationService;

import lombok.RequiredArgsConstructor;

// Fonctionnalité désactivée : le mécanisme d'invitation par code façon
// Google Classroom (ENC-XXXXX) n'est plus utilisé pour lier encadrant et
// étudiant. L'affectation se fait désormais soit manuellement par le chef
// de département (ResponsableController), soit par import de fichier
// (voir ResponsableController#importerAffectations), pour des raisons de
// confidentialité — le code et le service restent en place mais ne sont
// plus exposés en tant que routes REST actives.
// @RestController("invitationControllerM1")
// @RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {
	private final InvitationService invitationService;

    @PostMapping("/envoyer")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ResponseEntity<Void> envoyer(
            @RequestParam Long encadrantId,
            @RequestParam Long etudiantId) {
        invitationService.envoyerInvitation(encadrantId, etudiantId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accepter")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<Void> accepter(@RequestParam String token) {
        invitationService.accepterInvitation(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refuser")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<Void> refuser(@RequestParam String token) {
        invitationService.refuserInvitation(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/historique")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR','ROLE_DEPT_MANAGER')")
    public ResponseEntity<List<InvitationResponse>> historique(
            @RequestParam Long encadrantId) {
        return ResponseEntity.ok(invitationService.getHistoriqueInvitations(encadrantId));
    }
}
