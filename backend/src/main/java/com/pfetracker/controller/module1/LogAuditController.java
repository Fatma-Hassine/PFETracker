package com.pfetracker.controller.module1;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfetracker.dto.module1.LogAuditResponse;
import com.pfetracker.service.module1.LogAuditService;

import lombok.RequiredArgsConstructor;

@RestController("logAuditControllerM1")
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogAuditController {
	 private final LogAuditService logAuditService;

	    // ── GET /api/logs/departement/{deptId} ── DEPT_MANAGER uniquement ────────
	    // Le responsable consulte les logs des utilisateurs de son département
	    @GetMapping("/departement/{deptId}")
	    @PreAuthorize("hasRole('ROLE_DEPT_MANAGER')")
	    public ResponseEntity<List<LogAuditResponse>> getLogsDepartement(
	            @PathVariable Long deptId) {
	        return ResponseEntity.ok(logAuditService.getLogsDepartement(deptId));
	    }

	    // ── GET /api/logs/utilisateur/{userId} ── DEPT_MANAGER ───────────────────
	    @GetMapping("/utilisateur/{userId}")
	    @PreAuthorize("hasRole('ROLE_DEPT_MANAGER')")
	    public ResponseEntity<List<LogAuditResponse>> getLogsUtilisateur(
	            @PathVariable Long userId) {
	        return ResponseEntity.ok(logAuditService.getLogsUtilisateur(userId));
	    }
}
