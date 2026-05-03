package com.pfetracker.dto.module2;

import com.pfetracker.entity.module2.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Élément généré par l'assistant IA.
 *
 * Un élément peut représenter :
 * - une tâche
 * - une user story
 * - une spécification
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiGeneratedItemDTO {

    private String title;

    private String description;

    private Priority priority;

    private Integer estimatedHours;

    /**
     * Type textuel affichable côté frontend.
     * Exemple : TASK, USER_STORY, SPECIFICATION.
     */
    private String itemType;
}