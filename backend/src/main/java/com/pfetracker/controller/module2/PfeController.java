package com.pfetracker.controller.module2;

import com.pfetracker.dto.module2.CreatePfeRequest;
import com.pfetracker.dto.module2.UpdateProjectSheetRequest;
import com.pfetracker.dto.module2.ValidateProjectSheetRequest;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.service.module2.PfeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller principal du Module 2 pour gérer les PFE.
 */
@RestController
@RequestMapping("/api/v2/pfeTrack/pfes")
@RequiredArgsConstructor
public class PfeController {

    private final PfeService pfeService;

    /**
     * Retourne les PFE selon l'utilisateur courant.
     * Étudiant -> ses PFE
     * Encadrant -> les PFE qu'il encadre
     * Admin/Responsable -> tous les PFE
     */
    @GetMapping
    public List<Pfe> getMyPfes() {
        return pfeService.getMyPfes();
    }

    /**
     * Retourne un PFE par son ID.
     */
    @GetMapping("/{pfeId}")
    public Pfe getPfeById(@PathVariable Long pfeId) {
        return pfeService.getPfeById(pfeId);
    }

    /**
     * Crée un PFE avec ses 6 jalons par défaut.
     */
    @PostMapping
    public Pfe createPfe(@Valid @RequestBody CreatePfeRequest request) {
        return pfeService.createPfe(request);
    }

    /**
     * Met à jour la fiche projet.
     */
    @PutMapping("/{pfeId}/project-sheet")
    public Pfe updateProjectSheet(
            @PathVariable Long pfeId,
            @Valid @RequestBody UpdateProjectSheetRequest request
    ) {
        return pfeService.updateProjectSheet(pfeId, request);
    }

    /**
     * Valide ou refuse la fiche projet.
     */
    @PostMapping("/{pfeId}/validate-project-sheet")
    public Pfe validateProjectSheet(
            @PathVariable Long pfeId,
            @RequestBody ValidateProjectSheetRequest request
    ) {
        return pfeService.validateProjectSheet(pfeId, request);
    }
}