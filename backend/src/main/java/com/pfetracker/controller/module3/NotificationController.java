package com.pfetracker.controller.module3;

import com.pfetracker.dto.module3.ApiResponse;
import com.pfetracker.dto.module3.MarkReadRequest;
import com.pfetracker.dto.module3.NotificationDTO;
import com.pfetracker.dto.module3.PageResponse;
import com.pfetracker.dto.module3.UserNotificationPreferenceDTO;
import com.pfetracker.security.module3.SecurityUtils;
import com.pfetracker.service.module3.NotificationService;
import com.pfetracker.service.module3.UserNotificationPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v3/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Gestion des notifications temps réel et par email")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserNotificationPreferenceService preferenceService;

    @GetMapping
    @Operation(summary = "Notifications de l'utilisateur", description = "Récupère les notifications avec pagination")
    public ResponseEntity<ApiResponse<PageResponse<NotificationDTO>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        PageResponse<NotificationDTO> notifications = notificationService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications récupérées"));
    }

    @GetMapping("/center")
    @Operation(summary = "Centre de notifications (dernières 30)", description = "Récupère les 30 dernières notifications pour le centre de notifications")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotificationCenter() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<NotificationDTO> notifications = notificationService.getNotificationCenter(userId, 30);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Centre de notifications récupéré"));
    }

    @GetMapping("/unread")
    @Operation(summary = "Notifications non lues")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications non lues récupérées"));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Compteur de notifications non lues")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        Long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count, "Compteur récupéré"));
    }

    @PutMapping("/read")
    @Operation(summary = "Marquer comme lues", description = "Marque une liste de notifications comme lues")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@Valid @RequestBody MarkReadRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.markAsRead(request.getNotificationIds(), userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Notifications marquées comme lues"));
    }

    @PutMapping("/read/all")
    @Operation(summary = "Tout marquer comme lu")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Toutes les notifications marquées comme lues"));
    }

    @PutMapping("/{notificationId}/read-by-action")
    @Operation(summary = "Marquer comme lu et rediriger", description = "Marque une notification comme lue au clic sur l'action")
    public ResponseEntity<ApiResponse<NotificationDTO>> markAsReadByAction(@PathVariable Long notificationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        NotificationDTO notification = notificationService.markAsReadByAction(notificationId, userId);
        return ResponseEntity.ok(ApiResponse.success(notification, "Notification marquée comme lue"));
    }

    @GetMapping("/preferences")
    @Operation(summary = "Récupérer les préférences de notifications")
    public ResponseEntity<ApiResponse<UserNotificationPreferenceDTO>> getPreferences() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserNotificationPreferenceDTO preferences = preferenceService.getOrCreatePreferences(userId);
        return ResponseEntity.ok(ApiResponse.success(preferences, "Préférences récupérées"));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Mettre à jour les préférences de notifications")
    public ResponseEntity<ApiResponse<UserNotificationPreferenceDTO>> updatePreferences(
            @Valid @RequestBody UserNotificationPreferenceDTO preferencesDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        UserNotificationPreferenceDTO updated = preferenceService.updatePreferences(userId, preferencesDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Préférences mises à jour"));
    }

    @PutMapping("/preferences/channels")
    @Operation(summary = "Configurer les canaux de notifications par type")
    public ResponseEntity<ApiResponse<UserNotificationPreferenceDTO>> updateChannelPreferences(
            @RequestParam String channel,
            @RequestParam String type,
            @RequestParam boolean enabled) {
        Long userId = SecurityUtils.getCurrentUserId();
        UserNotificationPreferenceDTO updated = preferenceService.updateChannelPreference(userId, channel, type, enabled);
        return ResponseEntity.ok(ApiResponse.success(updated, "Préférences de canal mises à jour"));
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Supprimer une notification")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long notificationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.deleteNotification(notificationId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification supprimée"));
    }
}

