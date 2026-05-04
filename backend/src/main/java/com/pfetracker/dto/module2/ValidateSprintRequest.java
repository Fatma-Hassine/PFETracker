package com.pfetracker.dto.module2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateSprintRequest {

    private Boolean accepted;

    private String supervisorComment;
}