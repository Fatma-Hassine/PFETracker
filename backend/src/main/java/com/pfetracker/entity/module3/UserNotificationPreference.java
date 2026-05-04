package com.pfetracker.entity.module3;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private Boolean emailEnabled = true;

    @Column(name = "in_app_enabled", nullable = false)
    @Builder.Default
    private Boolean inAppEnabled = true;

    @Column(name = "websocket_enabled", nullable = false)
    @Builder.Default
    private Boolean websocketEnabled = true;

    @Column(name = "message_notifications", nullable = false)
    @Builder.Default
    private Boolean messageNotifications = true;

    @Column(name = "meeting_notifications", nullable = false)
    @Builder.Default
    private Boolean meetingNotifications = true;

    @Column(name = "task_notifications", nullable = false)
    @Builder.Default
    private Boolean taskNotifications = true;

    @Column(name = "comment_notifications", nullable = false)
    @Builder.Default
    private Boolean commentNotifications = true;

    @Column(name = "alert_notifications", nullable = false)
    @Builder.Default
    private Boolean alertNotifications = true;

    @Column(name = "email_on_message", nullable = false)
    @Builder.Default
    private Boolean emailOnMessage = false;

    @Column(name = "email_on_meeting_reminder", nullable = false)
    @Builder.Default
    private Boolean emailOnMeetingReminder = true;

    @Column(name = "email_on_task_update", nullable = false)
    @Builder.Default
    private Boolean emailOnTaskUpdate = true;

    @Column(name = "email_on_comment_mention", nullable = false)
    @Builder.Default
    private Boolean emailOnCommentMention = true;

    @Column(name = "daily_digest_enabled", nullable = false)
    @Builder.Default
    private Boolean dailyDigestEnabled = false;

    @Column(name = "digest_send_time", length = 5)
    private String digestSendTime; // HH:mm format

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
