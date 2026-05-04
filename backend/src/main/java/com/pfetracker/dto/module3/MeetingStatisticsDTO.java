package com.pfetracker.dto.module3;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingStatisticsDTO {

    private Long totalMeetings;
    private Long completedMeetings;
    private Long cancelledMeetings;
    private Long pendingMeetings;
    private Double averageCompletionRate;
    private Map<String, Long> meetingsByStatus;
    private Map<String, Long> meetingsByType;
    private Long totalParticipants;
}
