package com.pfetracker.dto.module2;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateSprintRequest {

    @NotBlank
    private String objective;

    private LocalDate startDate;

    private LocalDate endDate;
}