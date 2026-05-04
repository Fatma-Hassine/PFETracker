package com.pfetracker.service.module3;

import com.pfetracker.dto.module3.UserNotificationPreferenceDTO;
import com.pfetracker.entity.module3.UserNotificationPreference;
import com.pfetracker.exception.module3.ResourceNotFoundException;
import com.pfetracker.mapper.module3.UserNotificationPreferenceMapper;
import com.pfetracker.repository.module3.UserNotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserNotificationPreferenceService {

    private final UserNotificationPreferenceRepository preferenceRepository;
    private final UserNotificationPreferenceMapper preferenceMapper;

    @Transactional(readOnly = true)
    public UserNotificationPreferenceDTO getOrCreatePreferences(Long userId) {
        UserNotificationPreference preferences = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));
        return preferenceMapper.toDTO(preferences);
    }

    private UserNotificationPreference createDefaultPreferences(Long userId) {
        UserNotificationPreference preferences = UserNotificationPreference.builder()
                .userId(userId)
                .emailEnabled(true)
                .inAppEnabled(true)
                .websocketEnabled(true)
                .messageNotifications(true)
                .meetingNotifications(true)
                .taskNotifications(true)
                .commentNotifications(true)
                .alertNotifications(true)
                .emailOnMessage(false)
                .emailOnMeetingReminder(true)
                .emailOnTaskUpdate(true)
                .emailOnCommentMention(true)
                .dailyDigestEnabled(false)
                .digestSendTime("08:00")
                .build();

        return preferenceRepository.save(preferences);
    }

    public UserNotificationPreferenceDTO updatePreferences(Long userId, UserNotificationPreferenceDTO preferencesDTO) {
        UserNotificationPreference preferences = preferenceRepository.findByUserId(userId)
                .orElse(createDefaultPreferences(userId));

        preferences.setEmailEnabled(preferencesDTO.getEmailEnabled() != null ? preferencesDTO.getEmailEnabled() : preferences.getEmailEnabled());
        preferences.setInAppEnabled(preferencesDTO.getInAppEnabled() != null ? preferencesDTO.getInAppEnabled() : preferences.getInAppEnabled());
        preferences.setWebsocketEnabled(preferencesDTO.getWebsocketEnabled() != null ? preferencesDTO.getWebsocketEnabled() : preferences.getWebsocketEnabled());
        preferences.setMessageNotifications(preferencesDTO.getMessageNotifications() != null ? preferencesDTO.getMessageNotifications() : preferences.getMessageNotifications());
        preferences.setMeetingNotifications(preferencesDTO.getMeetingNotifications() != null ? preferencesDTO.getMeetingNotifications() : preferences.getMeetingNotifications());
        preferences.setTaskNotifications(preferencesDTO.getTaskNotifications() != null ? preferencesDTO.getTaskNotifications() : preferences.getTaskNotifications());
        preferences.setCommentNotifications(preferencesDTO.getCommentNotifications() != null ? preferencesDTO.getCommentNotifications() : preferences.getCommentNotifications());
        preferences.setAlertNotifications(preferencesDTO.getAlertNotifications() != null ? preferencesDTO.getAlertNotifications() : preferences.getAlertNotifications());
        preferences.setEmailOnMessage(preferencesDTO.getEmailOnMessage() != null ? preferencesDTO.getEmailOnMessage() : preferences.getEmailOnMessage());
        preferences.setEmailOnMeetingReminder(preferencesDTO.getEmailOnMeetingReminder() != null ? preferencesDTO.getEmailOnMeetingReminder() : preferences.getEmailOnMeetingReminder());
        preferences.setEmailOnTaskUpdate(preferencesDTO.getEmailOnTaskUpdate() != null ? preferencesDTO.getEmailOnTaskUpdate() : preferences.getEmailOnTaskUpdate());
        preferences.setEmailOnCommentMention(preferencesDTO.getEmailOnCommentMention() != null ? preferencesDTO.getEmailOnCommentMention() : preferences.getEmailOnCommentMention());
        preferences.setDailyDigestEnabled(preferencesDTO.getDailyDigestEnabled() != null ? preferencesDTO.getDailyDigestEnabled() : preferences.getDailyDigestEnabled());
        if (preferencesDTO.getDigestSendTime() != null) {
            preferences.setDigestSendTime(preferencesDTO.getDigestSendTime());
        }

        UserNotificationPreference updated = preferenceRepository.save(preferences);
        log.info("Preferences updated for user {}", userId);
        return preferenceMapper.toDTO(updated);
    }

    public UserNotificationPreferenceDTO updateChannelPreference(Long userId, String channel, String type, boolean enabled) {
        UserNotificationPreference preferences = preferenceRepository.findByUserId(userId)
                .orElse(createDefaultPreferences(userId));

        switch (channel.toLowerCase()) {
            case "email":
                switch (type.toLowerCase()) {
                    case "message" -> preferences.setEmailOnMessage(enabled);
                    case "meeting" -> preferences.setEmailOnMeetingReminder(enabled);
                    case "task" -> preferences.setEmailOnTaskUpdate(enabled);
                    case "mention" -> preferences.setEmailOnCommentMention(enabled);
                }
                break;
            case "in_app":
                switch (type.toLowerCase()) {
                    case "message" -> preferences.setMessageNotifications(enabled);
                    case "meeting" -> preferences.setMeetingNotifications(enabled);
                    case "task" -> preferences.setTaskNotifications(enabled);
                    case "comment" -> preferences.setCommentNotifications(enabled);
                    case "alert" -> preferences.setAlertNotifications(enabled);
                }
                break;
            case "websocket":
                preferences.setWebsocketEnabled(enabled);
                break;
        }

        UserNotificationPreference updated = preferenceRepository.save(preferences);
        log.info("Channel preference updated for user {} - channel={}, type={}, enabled={}", userId, channel, type, enabled);
        return preferenceMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public boolean shouldNotifyViaChannel(Long userId, String channel, String type) {
        UserNotificationPreference preferences = preferenceRepository.findByUserId(userId)
                .orElse(createDefaultPreferences(userId));

        return switch (channel.toLowerCase()) {
            case "email" -> preferences.getEmailEnabled() && matchEmailType(preferences, type);
            case "in_app" -> preferences.getInAppEnabled() && matchInAppType(preferences, type);
            case "websocket" -> preferences.getWebsocketEnabled();
            default -> true;
        };
    }

    private boolean matchEmailType(UserNotificationPreference prefs, String type) {
        return switch (type.toLowerCase()) {
            case "message" -> prefs.getEmailOnMessage();
            case "meeting" -> prefs.getEmailOnMeetingReminder();
            case "task" -> prefs.getEmailOnTaskUpdate();
            case "mention" -> prefs.getEmailOnCommentMention();
            default -> true;
        };
    }

    private boolean matchInAppType(UserNotificationPreference prefs, String type) {
        return switch (type.toLowerCase()) {
            case "message" -> prefs.getMessageNotifications();
            case "meeting" -> prefs.getMeetingNotifications();
            case "task" -> prefs.getTaskNotifications();
            case "comment" -> prefs.getCommentNotifications();
            case "alert" -> prefs.getAlertNotifications();
            default -> true;
        };
    }
}
