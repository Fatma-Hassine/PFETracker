package com.pfetracker.dto.module3;

import com.pfetracker.entity.module3.Meeting;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime meetingDate;
    private Integer duration;
    private Long createdBy;
    private String createdByName;
    private Long participantId;
    private String participantName;
    private Long pfeId;
    private Meeting.MeetingStatus status;
    private String meetingLink;
    private String meetProvider;
    private Boolean isOnline;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String report;
    private String rejectionReason;
    private Boolean reminderSent24h;
    private Boolean reminderSent15min;
}

