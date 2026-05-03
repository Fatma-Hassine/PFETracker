package com.pfetracker.dto.module2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateMilestonesBulkRequest {

    @Valid
    @NotEmpty
    private List<CreateMilestoneRequest> milestones;
}