package com.pfetracker.dto.module2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Requête utilisée quand l'utilisateur clique sur
 * "Ajouter toutes au projet".
 */
@Getter
@Setter
public class AiAddMultipleItemsRequest {

    @Valid
    @NotEmpty
    private List<AiAddGeneratedItemRequest> items;
}