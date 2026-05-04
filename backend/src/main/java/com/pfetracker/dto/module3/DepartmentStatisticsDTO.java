package com.pfetracker.dto.module3;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentStatisticsDTO {

    private Long departmentId;
    private String departmentName;
    private Integer studentCount;
    private Integer activePFECount;
    private Integer completedPFECount;
    private Double averageProgress;
    private Integer delayedPFECount;
    private Long totalMeetings;
    private Long completedMeetings;
}
