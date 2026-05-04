package com.pfetracker.controller.module1;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfetracker.dto.module1.NotificationResponse;
import com.pfetracker.service.module1.*;

import lombok.RequiredArgsConstructor;

@RestController("notificationControllerM1") 
@RequestMapping("/notifications")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;

    @GetMapping("/moi")
    public ResponseEntity<List<NotificationResponse>> getMesNotifications(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(notificationService.getMesNotifications(userId));
    }

    @PatchMapping("/{id}/lire")
    public ResponseEntity<Void> marquerLu(@PathVariable Long id) {
        notificationService.marquerLu(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/tout-lire")
    public ResponseEntity<Void> marquerToutesLues(
            @AuthenticationPrincipal Long userId) {
        notificationService.marquerToutesLues(userId);
        return ResponseEntity.ok().build();
    }
}
