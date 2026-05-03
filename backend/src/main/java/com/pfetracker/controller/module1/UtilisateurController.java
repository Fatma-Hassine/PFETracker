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
import com.pfetracker.dto.*;

@RestController("utilisateurControllerM1")
@RequestMapping("/api/utilisateurs")
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
    @PreAuthorize("hasAnyRole('ROLE_DEPT_MANAGER','ROLE_DIRECTOR')")
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
