package com.pfetracker.dto.module2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class WeeklyReportDTO {
    private Long encadrantId;
    private String encadrantNom;
    private LocalDate genereLe;
    private List<StudentWeeklySummaryDTO> etudiants;
}
