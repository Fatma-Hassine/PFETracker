package com.pfetracker.dto.module1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AffectationImportResultDTO {

    private int totalRows;

    private int createdCount;

    private int skippedCount;

    private int errorCount;

    private List<String> messages;
}
