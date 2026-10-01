package com.pfetracker.service.module3;

import com.pfetracker.dto.module3.MessageDTO;
import com.pfetracker.dto.module3.NotificationDTO;
import com.pfetracker.dto.module3.PageResponse;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.entity.module3.Comment;
import com.pfetracker.entity.module3.Meeting;
import com.pfetracker.entity.module3.NotificationM3;
import com.pfetracker.exception.module3.ResourceNotFoundException;
import com.pfetracker.exception.module3.UnauthorizedException;
import com.pfetracker.mapper.module3.NotificationMapper;
import com.pfetracker.repository.module1.UtilisateurRepository;
import com.pfetracker.repository.module3.NotificationRepositoryM3;
import com.pfetracker.websocket.module3.NotificationWebSocketController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepositoryM3 notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationWebSocketController webSocketController;
    private final UtilisateurRepository userRepository;
    private final EmailService emailService;

    public NotificationDTO createNotification(Long userId, String message, NotificationM3.NotificationType type,
                                               Long relatedId, String relatedType, String actionUrl) {
        NotificationM3 notification = NotificationM3.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .isRead(false)
                .relatedId(relatedId)
                .relatedType(relatedType)
                .actionUrl(actionUrl)
                .build();

        NotificationM3 saved = notificationRepository.save(notification);
        NotificationDTO dto = notificationMapper.toDTO(saved);

        webSocketController.sendNotification(userId, dto);
        log.info("Notification created: id={}, user={}, type={}", saved.getId(), userId, type);

        return dto;
    }

    public void createMessageNotification(Long receiverId, MessageDTO message) {
        String msg = "Nouveau message de " + message.getSenderName();
        createNotification(receiverId, msg, NotificationM3.NotificationType.MESSAGE,
                message.getId(), "MESSAGE", "/messages/" + message.getPfeId());
    }

    public void createCommentNotification(Long userId, Comment comment, Task task) {
        String msg = "Nouveau commentaire sur la tâche: " + task.getTitle();
        createNotification(userId, msg, NotificationM3.NotificationType.TASK,
                comment.getId(), "COMMENT", "/tasks/" + task.getId() + "/comments");
    }

    public void createMentionNotification(Long userId, Comment comment, Pfe pfe) {
        String msg = "Vous avez été mentionné dans un commentaire";
        createNotification(userId, msg, NotificationM3.NotificationType.TASK,
                comment.getId(), "COMMENT", "/tasks/" + comment.getTaskId() + "/comments");
    }

    public void createMeetingNotification(Long userId, Meeting meeting, String action) {
        String msg = switch (action) {
            case "CREATED" -> "Nouvelle réunion planifiée: " + meeting.getTitle();
            case "UPDATED" -> "Réunion modifiée: " + meeting.getTitle();
            case "CANCELLED" -> "Réunion annulée: " + meeting.getTitle();
            case "REMINDER_24H" -> "Rappel: Réunion dans 24h - " + meeting.getTitle();
            case "REMINDER_15MIN" -> "Rappel: Réunion dans 15 min - " + meeting.getTitle();
            default -> "Mise à jour de réunion: " + meeting.getTitle();
        };

        NotificationM3.NotificationType type = action.contains("REMINDER") 
                ? NotificationM3.NotificationType.MEETING 
                : NotificationM3.NotificationType.MEETING;

        createNotification(userId, msg, type, meeting.getId(), "MEETING", "/meetings/" + meeting.getId());
    }

    public void createTaskNotification(Long userId, Task task, String action) {
        String msg = switch (action) {
            case "ASSIGNED" -> "Nouvelle tâche assignée: " + task.getTitle();
            case "SUBMITTED" -> "Tâche soumise pour révision: " + task.getTitle();
            case "VALIDATED" -> "Tâche validée: " + task.getTitle();
            case "TO_CORRECT" -> "Corrections demandées: " + task.getTitle();
            default -> "Mise à jour de tâche: " + task.getTitle();
        };

        createNotification(userId, msg, NotificationM3.NotificationType.TASK,
                task.getId(), "TASK", "/tasks/" + task.getId());
    }

    public void createAlertNotification(Long userId, String alertMessage, String severity) {
        createNotification(userId, alertMessage, NotificationM3.NotificationType.ALERT,
                null, "ALERT", "/dashboard");
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationDTO> getUserNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationM3> notifications = notificationRepository.findByUserId(userId, pageable);

        return PageResponse.<NotificationDTO>builder()
                .content(notificationMapper.toDTOList(notifications.getContent()))
                .pageNumber(notifications.getNumber())
                .pageSize(notifications.getSize())
                .totalElements(notifications.getTotalElements())
                .totalPages(notifications.getTotalPages())
                .last(notifications.isLast())
                .first(notifications.isFirst())
                .build();
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationMapper.toDTOList(notificationRepository.findUnreadByUserId(userId));
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    public void markAsRead(List<Long> notificationIds, Long userId) {
        notificationRepository.markAsRead(notificationIds, userId, LocalDateTime.now());
        log.info("Notifications {} marked as read by user {}", notificationIds, userId);
    }

    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId, LocalDateTime.now());
        log.info("All notifications marked as read by user {}", userId);
    }

    @Async("emailExecutor")
    public void sendEmailNotification(Long userId, String subject, String body) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getEmail() != null) {
                emailService.sendEmail(user.getEmail(), subject, body);
                log.info("Email sent to user {}: {}", userId, subject);
            }
        });
    }

    @Async("emailExecutor")
    public void sendMeetingReminderEmail(Long userId, Meeting meeting, int hoursBefore) {
        String subject = "Rappel de réunion - " + meeting.getTitle();
        String body = String.format(
            "Bonjour,%n%n" +
            "Rappel: Vous avez une réunion dans %d heures.%n%n" +
            "Titre: %s%n" +
            "Date: %s%n" +
            "Durée: %d minutes%n" +
            "Lien: %s%n%n" +
            "Cordialement,%nPFETracker",
            hoursBefore, meeting.getTitle(), meeting.getMeetingDate(), 
            meeting.getDuration(), meeting.getMeetingLink() != null ? meeting.getMeetingLink() : "Non spécifié"
        );
        sendEmailNotification(userId, subject, body);
    }

    @Async("emailExecutor")
    public void sendCriticalAlertEmail(Long userId, String alertMessage) {
        String subject = "⚠️ Alerte critique - PFETracker";
        String body = String.format(
            "Bonjour,%n%n" +
            "Une alerte critique a été détectée:%n%n" +
            "%s%n%n" +
            "Veuillez consulter votre tableau de bord pour plus de détails.%n%n" +
            "Cordialement,%nPFETracker",
            alertMessage
        );
        sendEmailNotification(userId, subject, body);
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationCenter(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<NotificationM3> notifications = notificationRepository.findByUserId(userId, pageable);
        return notificationMapper.toDTOList(notifications.getContent());
    }

    public NotificationDTO markAsReadByAction(Long notificationId, Long userId) {
        NotificationM3 notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée"));

        if (!notification.getUserId().equals(userId)) {
            throw new UnauthorizedException("Vous ne pouvez pas accéder à cette notification");
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        NotificationM3 updated = notificationRepository.save(notification);
        log.info("Notification {} marked as read by action from user {}", notificationId, userId);
        return notificationMapper.toDTO(updated);
    }

    public void deleteNotification(Long notificationId, Long userId) {
        NotificationM3 notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée"));

        if (!notification.getUserId().equals(userId)) {
            throw new UnauthorizedException("Vous ne pouvez pas supprimer cette notification");
        }

        notificationRepository.delete(notification);
        log.info("Notification {} deleted by user {}", notificationId, userId);
    }
}

