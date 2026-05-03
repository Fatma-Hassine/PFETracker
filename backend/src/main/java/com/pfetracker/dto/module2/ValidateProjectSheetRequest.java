package com.pfetracker.dto.module2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateProjectSheetRequest {

    private Boolean accepted;

    private String comment;
}