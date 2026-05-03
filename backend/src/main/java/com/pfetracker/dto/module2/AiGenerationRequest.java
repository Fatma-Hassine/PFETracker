package com.pfetracker.dto.module2;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Requête envoyée par le frontend quand l'étudiant clique sur
 * "Générer avec l'IA".
 */
@Getter
@Setter
public class AiGenerationRequest {

    /**
     * Type de génération :
     * TASKS, USER_STORIES ou SPECIFICATIONS.
     */
    @NotNull
    private AiGenerationType type;

    /**
     * Texte écrit par l'étudiant.
     * Exemple : "Je veux créer un système d'authentification avec Spring Boot".
     */
    @NotBlank
    private String prompt;

    /**
     * PFE concerné.
     */
    private Long pfeId;

    /**
     * Jalon dans lequel les éléments pourront être ajoutés.
     * Important pour le bouton "Ajouter au projet".
     */
    private Long milestoneId;
}