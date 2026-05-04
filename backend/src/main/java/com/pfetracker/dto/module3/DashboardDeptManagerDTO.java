package com.pfetracker.dto.module3;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDeptManagerDTO {

    private Long managerId;
    private String departmentName;
    private Integer totalStudents;
    private Integer activePFEs;
    private Integer completedPFEs;
    private Integer delayedPFEs;
    private Double averageProgress;
    private Long totalMeetings;
    private Long completedMeetings;
    private Integer overallInactiveStudents;
    private List<StudentSummaryDTO> students;
    private List<AlertDTO> criticalAlerts;
    private List<MeetingDTO> upcomingMeetings;
    private List<NotificationDTO> recentNotifications;
    private MeetingStatisticsDTO meetingStats;
}
