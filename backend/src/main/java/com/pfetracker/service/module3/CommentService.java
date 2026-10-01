package com.pfetracker.service.module3;

import com.pfetracker.dto.module3.CommentDTO;
import com.pfetracker.dto.module3.CreateCommentRequest;
import com.pfetracker.dto.module3.PageResponse;
import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.entity.module3.Comment;
import com.pfetracker.exception.module3.ResourceNotFoundException;
import com.pfetracker.exception.module3.UnauthorizedException;
import com.pfetracker.mapper.module3.CommentMapper;
import com.pfetracker.repository.module1.UtilisateurRepository;
import com.pfetracker.repository.module2.Module2TaskRepository;
import com.pfetracker.repository.module2.PfeRepository;
import com.pfetracker.repository.module3.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final Module2TaskRepository taskRepository;
    private final PfeRepository pfeRepository;
    private final UtilisateurRepository userRepository;
    private final NotificationService notificationService;

    public CommentDTO addComment(Long userId, CreateCommentRequest request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée: " + request.getTaskId()));

        Pfe pfe = pfeRepository.findById(task.getMilestone().getPfe().getId())
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvé"));

        if (!isAuthorizedToComment(userId, pfe)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à commenter cette tâche");
        }

        Comment comment = Comment.builder()
                .taskId(request.getTaskId())
                .userId(userId)
                .content(request.getContent())
                .parentId(request.getParentId())
                .mentionedUserIds(request.getMentionedUserIds() != null ? request.getMentionedUserIds() : List.of())
                .attachmentUrl(request.getAttachmentUrl())
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("Comment added: id={}, task={}, user={}", saved.getId(), request.getTaskId(), userId);

        notifyMentionedUsers(saved, pfe);
        notifyTaskParticipants(saved, task, pfe, userId);

        return enrichComment(commentMapper.toDTO(saved));
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentDTO> getCommentsByTask(Long taskId, Long userId, int page, int size) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée"));

        Pfe pfe = pfeRepository.findById(task.getMilestone().getPfe().getId())
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvé"));

        if (!isAuthorizedToComment(userId, pfe)) {
            throw new UnauthorizedException("Accès non autorisé");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> comments = commentRepository.findByTaskId(taskId, pageable);

        List<CommentDTO> enriched = comments.getContent().stream()
                .map(c -> enrichComment(commentMapper.toDTO(c)))
                .collect(Collectors.toList());

        return PageResponse.<CommentDTO>builder()
                .content(enriched)
                .pageNumber(comments.getNumber())
                .pageSize(comments.getSize())
                .totalElements(comments.getTotalElements())
                .totalPages(comments.getTotalPages())
                .last(comments.isLast())
                .first(comments.isFirst())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée"));

        Pfe pfe = pfeRepository.findById(task.getMilestone().getPfe().getId())
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvé"));

        if (!isAuthorizedToComment(userId, pfe)) {
            throw new UnauthorizedException("Accès non autorisé");
        }

        return commentRepository.findRootCommentsByTaskId(taskId).stream()
                .map(c -> enrichComment(commentMapper.toDTO(c)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getCommentCount(Long taskId) {
        return commentRepository.countByTaskId(taskId);
    }

    private boolean isAuthorizedToComment(Long userId, Pfe pfe) {
        return pfe.getStudentId().equals(userId) ||
               (pfe.getSupervisorId() != null && pfe.getSupervisorId().equals(userId));
    }

    private void notifyMentionedUsers(Comment comment, Pfe pfe) {
        if (comment.getMentionedUserIds() != null) {
            for (Long mentionedId : comment.getMentionedUserIds()) {
                notificationService.createMentionNotification(mentionedId, comment, pfe);
            }
        }
    }

    private void notifyTaskParticipants(Comment comment, Task task, Pfe pfe, Long commenterId) {
        Long notifyUserId = commenterId.equals(task.getAssignedStudentId()) ? pfe.getSupervisorId() : task.getAssignedStudentId();
        if (notifyUserId != null && !notifyUserId.equals(commenterId)) {
            notificationService.createCommentNotification(notifyUserId, comment, task);
        }
    }

    private CommentDTO enrichComment(CommentDTO dto) {
        userRepository.findById(dto.getUserId())
                .ifPresent(user -> {
                    dto.setUserName(user.getNomComplet());
                });

        if (dto.getMentionedUserIds() != null && !dto.getMentionedUserIds().isEmpty()) {
            List<Utilisateur> mentioned = userRepository.findByIdIn(dto.getMentionedUserIds());
            dto.setMentionedUserNames(mentioned.stream()
                    .map(Utilisateur::getNomComplet)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}

