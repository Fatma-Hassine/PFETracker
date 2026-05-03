package com.pfetracker.dto.module2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Réponse retournée au frontend après génération.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiGenerationResponse {

    private String message;

    private List<AiGeneratedItemDTO> items;
}