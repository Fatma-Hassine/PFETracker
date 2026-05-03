package com.pfetracker.dto.module2;

import com.pfetracker.entity.module2.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTaskRequest {

    @NotBlank
    private String title;

    private String description;

    private LocalDate deadline;

    private Priority priority = Priority.NORMAL;

    private Integer estimatedHours;

    private Long assignedStudentId;
}