package com.pfetracker.dto.module2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrentUserDTO {

    private Long userId;

    private String role;
}