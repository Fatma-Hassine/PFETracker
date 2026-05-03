package com.pfetracker.dto.module2;

import com.pfetracker.entity.module2.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiGeneratedItemDTO {

    private String title;

    private String description;

    private Priority priority;

    private Integer estimatedHours;

    private String itemType;

    private String expectedDeliverable;

    private LocalDate plannedStartDate;

    private LocalDate plannedEndDate;

    private Double weight;
}