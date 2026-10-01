package com.pfetracker.scheduler.module3;

import com.pfetracker.entity.module3.Meeting;
import com.pfetracker.repository.module3.MeetingRepository;
import com.pfetracker.service.module3.NotificationService;
import com.pfetracker.websocket.module3.NotificationWebSocketController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingReminderScheduler {

    private final MeetingRepository meetingRepository;
    private final NotificationService notificationService;
    private final NotificationWebSocketController webSocketController;

    /**
     * Rappel 24h avant la réunion - Exécuté toutes les heures
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void send24HourReminders() {
        log.info("Executing 24h reminder scheduler...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startWindow = now.plusHours(23);
        LocalDateTime endWindow = now.plusHours(25);

        List<Meeting> meetings = meetingRepository.findMeetingsFor24hReminder(startWindow, endWindow);

        for (Meeting meeting : meetings) {
            try {
                // Notification au créateur
                notificationService.createMeetingNotification(meeting.getCreatedBy(), meeting, "REMINDER_24H");
                notificationService.sendMeetingReminderEmail(meeting.getCreatedBy(), meeting, 24);

                // Notification au participant
                notificationService.createMeetingNotification(meeting.getParticipantId(), meeting, "REMINDER_24H");
                notificationService.sendMeetingReminderEmail(meeting.getParticipantId(), meeting, 24);

                // WebSocket temps réel
                webSocketController.sendMeetingReminder(meeting.getCreatedBy(), meeting.getId(), meeting.getTitle(), 24 * 60);
                webSocketController.sendMeetingReminder(meeting.getParticipantId(), meeting.getId(), meeting.getTitle(), 24 * 60);

                meeting.setReminderSent24h(true);
                meetingRepository.save(meeting);

                log.info("24h reminder sent for meeting: id={}, title={}", meeting.getId(), meeting.getTitle());
            } catch (Exception e) {
                log.error("Error sending 24h reminder for meeting {}: {}", meeting.getId(), e.getMessage());
            }
        }
    }

    /**
     * Rappel 15 min avant la réunion - Exécuté toutes les minutes
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void send15MinuteReminders() {
        log.debug("Executing 15min reminder scheduler...");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startWindow = now.plusMinutes(14);
        LocalDateTime endWindow = now.plusMinutes(16);

        List<Meeting> meetings = meetingRepository.findMeetingsFor15minReminder(startWindow, endWindow);

        for (Meeting meeting : meetings) {
            try {
                // Notification temps réel WebSocket uniquement (in-app)
                webSocketController.sendMeetingReminder(meeting.getCreatedBy(), meeting.getId(), meeting.getTitle(), 15);
                webSocketController.sendMeetingReminder(meeting.getParticipantId(), meeting.getId(), meeting.getTitle(), 15);

                // Notification in-app
                notificationService.createMeetingNotification(meeting.getCreatedBy(), meeting, "REMINDER_15MIN");
                notificationService.createMeetingNotification(meeting.getParticipantId(), meeting, "REMINDER_15MIN");

                meeting.setReminderSent15min(true);
                meetingRepository.save(meeting);

                log.info("15min reminder sent for meeting: id={}, title={}", meeting.getId(), meeting.getTitle());
            } catch (Exception e) {
                log.error("Error sending 15min reminder for meeting {}: {}", meeting.getId(), e.getMessage());
            }
        }
    }

    /**
     * Nettoyage des réunions passées - Exécuté une fois par jour à 3h du matin
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupPastMeetings() {
        log.info("Executing past meetings cleanup...");

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<Meeting> pastMeetings = meetingRepository.findAll().stream()
                .filter(m -> m.getMeetingDate().isBefore(yesterday) && 
                             m.getStatus() == Meeting.MeetingStatus.ACCEPTED &&
                             m.getReport() == null)
                .toList();

        for (Meeting meeting : pastMeetings) {
            try {
                // Notification pour ajouter un compte-rendu
                notificationService.createMeetingNotification(
                    meeting.getCreatedBy(), 
                    meeting, 
                    "REPORT_PENDING"
                );
                log.info("Report pending notification for meeting: id={}", meeting.getId());
            } catch (Exception e) {
                log.error("Error processing past meeting {}: {}", meeting.getId(), e.getMessage());
            }
        }
    }
}

