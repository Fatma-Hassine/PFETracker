package com.pfetracker.dto.module2;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProjectSheetRequest {

    @NotBlank
    private String title;

    private String description;

    private String problemStatement;

    private String objectives;

    private String technologies;

    private LocalDate startDate;

    private LocalDate defenseDate;
}