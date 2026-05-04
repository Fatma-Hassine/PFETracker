package com.pfetracker.dto.module3;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationPreferenceDTO {

    private Long id;
    private Boolean emailEnabled;
    private Boolean inAppEnabled;
    private Boolean websocketEnabled;
    private Boolean messageNotifications;
    private Boolean meetingNotifications;
    private Boolean taskNotifications;
    private Boolean commentNotifications;
    private Boolean alertNotifications;
    private Boolean emailOnMessage;
    private Boolean emailOnMeetingReminder;
    private Boolean emailOnTaskUpdate;
    private Boolean emailOnCommentMention;
    private Boolean dailyDigestEnabled;
    private String digestSendTime;
}
