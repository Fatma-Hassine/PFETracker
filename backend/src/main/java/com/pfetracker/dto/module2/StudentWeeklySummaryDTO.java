package com.pfetracker.dto.module2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudentWeeklySummaryDTO {
    private Long studentId;
    private String studentName;
    private String pfeTitle;
    private double progression;
    private int tachesValideesSemaine;
    private int tachesEnRetard;
    private int alertesActives;
}
