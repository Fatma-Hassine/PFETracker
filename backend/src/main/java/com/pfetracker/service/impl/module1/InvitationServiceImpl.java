package com.pfetracker.service.impl.module1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pfetracker.dto.module1.*;
import com.pfetracker.entity.module1.*;
import com.pfetracker.entity.module1.enums.StatutInvitation;
import com.pfetracker.entity.module1.enums.TypeNotification;
import com.pfetracker.exception.module1.BusinessException;
import com.pfetracker.repository.module1.*;
import com.pfetracker.service.module1.*;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InvitationServiceImpl implements InvitationService{
	 private final InvitationRepository invitationRepo;
	    private final EncadrantRepository encadrantRepo;
	    private final EtudiantRepository etudiantRepo;
	    private final ResponsableDepartementRepository responsableRepo;
	    private final NotificationService notificationService;
	    private final LogAuditService logAuditService;

	    @Override
	    public void envoyerInvitation(Long encadrantId, Long etudiantId) {
	        Encadrant encadrant = encadrantRepo.findById(encadrantId)
	                .orElseThrow(() -> new BusinessException("Encadrant introuvable"));
	        Etudiant etudiant = etudiantRepo.findById(etudiantId)
	                .orElseThrow(() -> new BusinessException("Étudiant introuvable"));

	        if (etudiant.getEncadrant() != null) {
	            throw new BusinessException("Cet étudiant a déjà un encadrant");
	        }

	        // Vérification de la limite de charge
	        ResponsableDepartement resp = responsableRepo
	                .findByDepartementId(encadrant.getDepartement().getId())
	                .orElseThrow();
	        long charge = etudiantRepo.countByEncadrantId(encadrantId);
	        if (charge >= resp.getLimiteEtudiantsParEncadrant()) {
	            throw new BusinessException("Limite d'étudiants atteinte pour cet encadrant");
	        }

	        Invitation invitation = new Invitation();
	        invitation.setToken(UUID.randomUUID().toString());
	        invitation.setEncadrant(encadrant);
	        invitation.setEtudiant(etudiant);
	        invitation.setStatut(StatutInvitation.EN_ATTENTE);
	        invitation.setDateExpiration(LocalDateTime.now().plusDays(7));
	        invitationRepo.save(invitation);

	        notificationService.creerNotification(etudiantId, TypeNotification.INVITATION,
	                "L'encadrant " + encadrant.getNomComplet() + " vous invite à rejoindre ses étudiants.");
	        logAuditService.log(encadrant, "ENVOI_INVITATION", null, "SUCCES",
	                "Étudiant : " + etudiant.getNomComplet());
	    }

	    @Override
	    public void accepterInvitation(String token) {
	        Invitation inv = getInvitationValide(token);

	        inv.getEtudiant().setEncadrant(inv.getEncadrant());
	        inv.setStatut(StatutInvitation.ACCEPTEE);

	        etudiantRepo.save(inv.getEtudiant());
	        invitationRepo.save(inv);

	        notificationService.creerNotification(inv.getEncadrant().getId(),
	                TypeNotification.INVITATION,
	                inv.getEtudiant().getNomComplet() + " a accepté votre invitation.");
	        logAuditService.log(inv.getEtudiant(), "ACCEPTATION_INVITATION", null, "SUCCES", null);
	    }

	    @Override
	    public void refuserInvitation(String token) {
	        Invitation inv = getInvitationValide(token);
	        inv.setStatut(StatutInvitation.REFUSEE);
	        invitationRepo.save(inv);

	        notificationService.creerNotification(inv.getEncadrant().getId(),
	                TypeNotification.INVITATION,
	                inv.getEtudiant().getNomComplet() + " a refusé votre invitation.");
	        logAuditService.log(inv.getEtudiant(), "REFUS_INVITATION", null, "SUCCES", null);
	    }

	    @Override
	    public List<InvitationResponse> getHistoriqueInvitations(Long encadrantId) {
	        return invitationRepo.findByEncadrantIdOrderByDateExpirationDesc(encadrantId)
	                .stream()
	                .map(InvitationResponse::fromEntity)
	                .collect(Collectors.toList());
	    }


	    private Invitation getInvitationValide(String token) {
	        Invitation inv = invitationRepo.findByToken(token)
	                .orElseThrow(() -> new BusinessException("Invitation introuvable"));

	        if (inv.getStatut() != StatutInvitation.EN_ATTENTE) {
	            throw new BusinessException("Cette invitation a déjà été traitée");
	        }
	        if (inv.estExpiree()) {
	            inv.setStatut(StatutInvitation.EXPIREE);
	            invitationRepo.save(inv);
	            throw new BusinessException("Cette invitation a expiré");
	        }
	        return inv;
	    }
}
