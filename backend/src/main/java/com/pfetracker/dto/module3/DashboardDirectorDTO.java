package com.pfetracker.dto.module3;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDirectorDTO {

    private Long directorId;
    private Integer totalDepartments;
    private Integer totalStudents;
    private Integer totalPFEs;
    private Integer completedPFEs;
    private Integer delayedPFEs;
    private Double globalAverageProgress;
    private Long totalMeetings;
    private Long completedMeetings;
    private Integer criticalAlertCount;
    private List<DepartmentStatisticsDTO> departmentStats;
    private List<AlertDTO> criticalAlerts;
    private Map<String, Long> pfeStatusDistribution;
    private Map<String, Double> departmentProgressComparison;
    private List<StudentSummaryDTO> topStudents;
    private List<StudentSummaryDTO> atRiskStudents;
    private MeetingStatisticsDTO systemMeetingStats;
    private List<NotificationDTO> recentNotifications;
}
