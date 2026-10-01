package com.pfetracker.service.impl.module1;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.pfetracker.dto.module1.NotificationResponse;
import com.pfetracker.entity.module1.NotificationM1;
import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.entity.module1.enums.TypeNotification;
import com.pfetracker.exception.module1.BusinessException;
import com.pfetracker.repository.module1.*;
import com.pfetracker.service.module1.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService{
	private final NotificationRepositoryM1 notificationRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final JavaMailSender mailSender;

    // MODIF : @Async — un serveur SMTP lent/indisponible (observé : ~36s
    // avant échec, bien au-delà des timeouts JavaMail configurés, probablement
    // une résolution DNS ou un blocage réseau non couvert par ces timeouts)
    // ne doit plus jamais bloquer la réponse HTTP de création de compte /
    // validation / etc. L'email part en arrière-plan ; son échec éventuel
    // est uniquement loggé, jamais renvoyé à l'appelant (cf. §7.2 cahier des
    // charges : temps de réponse API < 2s).
    @Override
    @Async
    public void envoyerEmailBienvenue(String email, String motDePasseTemp) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Bienvenue sur PFETracker");
            msg.setText("Votre compte a été créé.\n\n"
                    + "Mot de passe temporaire : " + motDePasseTemp + "\n"
                    + "Vous serez invité à le changer à votre première connexion.");
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("Email de bienvenue non envoyé à {} : {}", email, e.getMessage());
        }
    }

    @Override
    @Async
    public void envoyerLienReinitialisation(String email, String token) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Réinitialisation de mot de passe — PFETracker");
            msg.setText("Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n\n"
                    + "https://pfetracker.app/reset-password?token=" + token + "\n\n"
                    + "Ce lien est valable 1 heure et à usage unique.");
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("Email de réinitialisation non envoyé à {} : {}", email, e.getMessage());
        }
    }

    @Override
    public void creerNotification(Long destinataireId, TypeNotification type, String message) {
        Utilisateur destinataire = utilisateurRepo.findById(destinataireId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        NotificationM1 notif = new NotificationM1();
        notif.setType(type);
        notif.setMessage(message);
        notif.setDestinataire(destinataire);
        notificationRepo.save(notif);
    }

    @Override
    public List<NotificationResponse> getMesNotifications(Long userId) {
        return notificationRepo
                .findByDestinataireIdOrderByDateCreationDesc(userId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void marquerLu(Long notificationId) {
        NotificationM1 notif = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new BusinessException("Notification introuvable"));
        notif.marquerLu();
        notificationRepo.save(notif);
    }

    @Override
    public void marquerToutesLues(Long userId) {
        notificationRepo.findByDestinataireIdAndLuFalse(userId)
                .forEach(n -> {
                    n.marquerLu();
                    notificationRepo.save(n);
                });
    }
}
