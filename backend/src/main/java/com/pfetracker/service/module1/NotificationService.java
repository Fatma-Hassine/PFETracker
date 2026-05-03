package com.pfetracker.service.module1;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pfetracker.dto.module1.NotificationResponse;
import com.pfetracker.entity.module1.enums.TypeNotification;
@Service("notificationServiceM1")

public interface NotificationService {
	void envoyerEmailBienvenue(String email, String motDePasseTemp);
    void envoyerLienReinitialisation(String email, String token);
    void creerNotification(Long destinataireId, TypeNotification type, String message);
    List<NotificationResponse> getMesNotifications(Long userId);
    void marquerLu(Long notificationId);
    void marquerToutesLues(Long userId);
}
