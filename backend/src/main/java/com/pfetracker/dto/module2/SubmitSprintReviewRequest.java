package com.pfetracker.dto.module2;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitSprintReviewRequest {

    @NotBlank
    private String studentReport;
}