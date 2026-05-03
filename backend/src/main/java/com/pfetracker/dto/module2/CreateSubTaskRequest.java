package com.pfetracker.dto.module2;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSubTaskRequest {

    @NotBlank
    private String title;
}