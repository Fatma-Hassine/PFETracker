package com.pfetracker.controller.module2;

import com.pfetracker.dto.module2.AiAddGeneratedItemRequest;
import com.pfetracker.dto.module2.AiAddMultipleItemsRequest;
import com.pfetracker.dto.module2.AiGenerationRequest;
import com.pfetracker.dto.module2.AiGenerationResponse;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.service.module2.AiGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de l'assistant IA génératif.
 *
 * Il sert à :
 * - générer des tâches
 * - générer des user stories
 * - générer des spécifications
 * - ajouter une suggestion comme tâche réelle
 * - ajouter toutes les suggestions au projet
 */
@RestController
@RequestMapping("/api/module2/ai")
@RequiredArgsConstructor
public class AiGenerationController {

    private final AiGenerationService aiGenerationService;

    /**
     * Génère des suggestions IA.
     *
     * Exemple URL :
     * POST /api/module2/ai/generate
     */
    @PostMapping("/generate")
    public AiGenerationResponse generate(@Valid @RequestBody AiGenerationRequest request) {
        return aiGenerationService.generate(request);
    }

    /**
     * Ajoute une suggestion générée au projet comme tâche réelle.
     *
     * Exemple URL :
     * POST /api/module2/ai/add-item
     */
    @PostMapping("/add-item")
    public Task addGeneratedItem(@Valid @RequestBody AiAddGeneratedItemRequest request) {
        return aiGenerationService.addGeneratedItemAsTask(request);
    }

    /**
     * Ajoute toutes les suggestions générées au projet comme tâches réelles.
     *
     * Exemple URL :
     * POST /api/module2/ai/add-all
     */
    @PostMapping("/add-all")
    public List<Task> addAllGeneratedItems(@Valid @RequestBody AiAddMultipleItemsRequest request) {
        return aiGenerationService.addAllGeneratedItemsAsTasks(request);
    }
}