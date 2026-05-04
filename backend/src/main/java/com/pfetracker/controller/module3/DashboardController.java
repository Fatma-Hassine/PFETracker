package com.pfetracker.controller.module3;

import com.pfetracker.dto.module3.*;
import com.pfetracker.security.module3.SecurityUtils;
import com.pfetracker.service.module3.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/dashboard")
@RequiredArgsConstructor
@Tag(name = "Tableaux de bord", description = "APIs pour les tableaux de bord de tous les rôles")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Tableau de bord étudiant", description = "Récupère les données du tableau de bord pour un étudiant")
    public ResponseEntity<ApiResponse<DashboardStudentDTO>> getStudentDashboard() {
        Long studentId = SecurityUtils.getCurrentUserId();
        DashboardStudentDTO dashboard = dashboardService.getStudentDashboard(studentId);
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Tableau de bord étudiant récupéré"));
    }

    @GetMapping("/supervisor")
    @PreAuthorize("hasRole('SUPERVISOR')")
    @Operation(summary = "Tableau de bord encadrant", description = "Récupère les données du tableau de bord pour un encadrant")
    public ResponseEntity<ApiResponse<DashboardSupervisorDTO>> getSupervisorDashboard() {
        Long supervisorId = SecurityUtils.getCurrentUserId();
        DashboardSupervisorDTO dashboard = dashboardService.getSupervisorDashboard(supervisorId);
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Tableau de bord encadrant récupéré"));
    }

    @GetMapping("/dept-manager")
    @PreAuthorize("hasRole('DEPT_MANAGER')")
    @Operation(summary = "Tableau de bord responsable département", description = "Récupère les données du tableau de bord pour un responsable de département")
    public ResponseEntity<ApiResponse<DashboardDeptManagerDTO>> getDeptManagerDashboard() {
        Long managerId = SecurityUtils.getCurrentUserId();
        DashboardDeptManagerDTO dashboard = dashboardService.getDeptManagerDashboard(managerId);
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Tableau de bord responsable département récupéré"));
    }

    @GetMapping("/director")
    @PreAuthorize("hasRole('DIRECTOR')")
    @Operation(summary = "Tableau de bord directeur", description = "Récupère les données du tableau de bord pour un directeur des stages")
    public ResponseEntity<ApiResponse<DashboardDirectorDTO>> getDirectorDashboard() {
        Long directorId = SecurityUtils.getCurrentUserId();
        DashboardDirectorDTO dashboard = dashboardService.getDirectorDashboard(directorId);
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Tableau de bord directeur récupéré"));
    }
}

