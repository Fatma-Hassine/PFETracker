package com.pfetracker.controller.module3;

import com.pfetracker.dto.module3.ApiResponse;
import com.pfetracker.dto.module3.CommentDTO;
import com.pfetracker.dto.module3.CreateCommentRequest;
import com.pfetracker.dto.module3.PageResponse;
import com.pfetracker.security.module3.SecurityUtils;
import com.pfetracker.service.module3.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v3/comments")
@RequiredArgsConstructor
@Tag(name = "Commentaires", description = "Gestion des commentaires sur les tâches")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Ajouter un commentaire", description = "Ajoute un commentaire sur une tâche avec support des mentions @")
    public ResponseEntity<ApiResponse<CommentDTO>> addComment(@Valid @RequestBody CreateCommentRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        CommentDTO comment = commentService.addComment(userId, request);
        return ResponseEntity.ok(ApiResponse.success(comment, "Commentaire ajouté avec succès"));
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Commentaires par tâche", description = "Récupère les commentaires d'une tâche avec pagination")
    public ResponseEntity<ApiResponse<PageResponse<CommentDTO>>> getCommentsByTask(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        PageResponse<CommentDTO> comments = commentService.getCommentsByTask(taskId, userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(comments, "Commentaires récupérés"));
    }

    @GetMapping("/task/{taskId}/all")
    @Operation(summary = "Tous les commentaires par tâche", description = "Récupère tous les commentaires sans pagination")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getAllCommentsByTask(@PathVariable Long taskId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<CommentDTO> comments = commentService.getCommentsByTask(taskId, userId);
        return ResponseEntity.ok(ApiResponse.success(comments, "Commentaires récupérés"));
    }

    @GetMapping("/task/{taskId}/count")
    @Operation(summary = "Compteur de commentaires")
    public ResponseEntity<ApiResponse<Long>> getCommentCount(@PathVariable Long taskId) {
        Long count = commentService.getCommentCount(taskId);
        return ResponseEntity.ok(ApiResponse.success(count, "Compteur récupéré"));
    }
}

