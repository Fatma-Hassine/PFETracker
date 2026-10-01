package com.pfetracker.controller.module1;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pfetracker.dto.module1.ProfilUpdateRequest;
import com.pfetracker.dto.module1.UtilisateurResponse;
import com.pfetracker.service.module1.*;

import lombok.RequiredArgsConstructor;

import java.io.IOException;


@RestController("utilisateurControllerM1")
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {
	private final UtilisateurService utilisateurService;

    @GetMapping("/moi")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UtilisateurResponse> getMoi(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(utilisateurService.getById(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CHEF_DEPARTEMENT','ROLE_DIRECTEUR')")
    public ResponseEntity<UtilisateurResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getById(id));
    }

    @PutMapping("/moi")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UtilisateurResponse> mettreAJourProfil(
            @AuthenticationPrincipal Long userId,
            @RequestBody ProfilUpdateRequest request) {
        return ResponseEntity.ok(utilisateurService.mettreAJourProfil(userId, request));
    }

   
}
