package com.pfetracker.dto.module2;

import com.pfetracker.entity.module2.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Requête utilisée quand l'utilisateur clique sur "Ajouter".
 *
 * Même si l'élément généré est une User Story ou une Spécification,
 * on peut l'ajouter au projet comme une tâche dans un jalon.
 */
@Getter
@Setter
public class AiAddGeneratedItemRequest {

    @NotNull
    private Long milestoneId;

    @NotBlank
    private String title;

    private String description;

    private Priority priority = Priority.NORMAL;

    private Integer estimatedHours;

    private String itemType;
}