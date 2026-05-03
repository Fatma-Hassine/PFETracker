package com.pfetracker.dto.module2;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateMilestoneRequest {

    private LocalDate plannedStartDate;

    private LocalDate plannedEndDate;

    private Double weight;

    private String supervisorComment;
}