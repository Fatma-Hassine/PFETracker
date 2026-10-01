package com.pfetracker.controller.module3;

import com.pfetracker.security.module3.SecurityUtils;
import com.pfetracker.service.module3.GoogleCalendarService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Autorisation OAuth2 Google Calendar (cahier des charges §6.3.1).
 * /authorize est protégé (JWT PFETracker) — /callback est public car c'est
 * Google qui redirige le navigateur dessus, sans notre token.
 */
@RestController
@RequiredArgsConstructor
public class GoogleAuthController {

    private final GoogleCalendarService googleCalendarService;

    @GetMapping("/v3/meetings/google/authorize")
    public ResponseEntity<String> autoriser() {
        String email = SecurityUtils.getCurrentUser().getEmail();
        String url = googleCalendarService.getOAuth2AuthorizationUrl(email);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(url);
    }

    @GetMapping("/v3/public/google/callback")
    public ResponseEntity<Void> callback(
            @RequestParam("code") String code,
            @RequestParam("state") String userEmail) {

        boolean succes = googleCalendarService.handleOAuth2Callback(userEmail, code);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/encadrant/reunions?googleCalendar=" + (succes ? "ok" : "erreur")))
                .build();
    }

    @DeleteMapping("/v3/meetings/google/revoke")
    public ResponseEntity<Void> revoquer() {
        String email = SecurityUtils.getCurrentUser().getEmail();
        googleCalendarService.revokeGoogleCalendarAccess(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v3/meetings/google/status")
    public ResponseEntity<Boolean> statut() {
        String email = SecurityUtils.getCurrentUser().getEmail();
        return ResponseEntity.ok(googleCalendarService.hasGoogleCalendarAuthorization(email));
    }
}
