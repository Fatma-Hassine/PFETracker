package com.pfetracker.dto.module2;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateMilestoneRequest {

    @NotBlank
    private String title;

    private String description;

    private String expectedDeliverable;

    private LocalDate plannedStartDate;

    private LocalDate plannedEndDate;

    private Double weight;
}