package com.pfetracker.service.module3;

import com.pfetracker.dto.module3.MessageDTO;
import com.pfetracker.dto.module3.PageResponse;
import com.pfetracker.dto.module3.SendMessageRequest;
import com.pfetracker.entity.module3.Message;
import com.pfetracker.entity.module3.PFE;
import com.pfetracker.exception.module3.ResourceNotFoundException;
import com.pfetracker.exception.module3.UnauthorizedException;
import com.pfetracker.mapper.module3.MessageMapper;
import com.pfetracker.repository.module3.MessageRepository;
import com.pfetracker.repository.module3.PFERepository;
import com.pfetracker.repository.module3.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final PFERepository pfeRepository;
    private final UserRepository userRepository;

    public MessageDTO sendMessage(Long senderId, SendMessageRequest request) {
        PFE pfe = pfeRepository.findById(request.getPfeId())
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvÃ©: " + request.getPfeId()));

        if (!isParticipantInPFE(senderId, pfe) || !isParticipantInPFE(request.getReceiverId(), pfe)) {
            throw new UnauthorizedException("Vous n'Ãªtes pas autorisÃ© Ã  envoyer des messages dans ce PFE");
        }

        Message message = Message.builder()
                .pfeId(request.getPfeId())
                .senderId(senderId)
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .status(Message.MessageStatus.UNREAD)
                .attachmentUrl(request.getAttachmentUrl())
                .attachmentName(request.getAttachmentName())
                .build();

        Message saved = messageRepository.save(message);
        log.info("Message sent: id={}, from={}, to={}, pfe={}", saved.getId(), senderId, request.getReceiverId(), request.getPfeId());

        return enrichWithUserNames(messageMapper.toDTO(saved));
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageDTO> getConversation(Long pfeId, Long userId, Long otherUserId, int page, int size) {
        PFE pfe = pfeRepository.findById(pfeId)
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvÃ©"));

        if (!isParticipantInPFE(userId, pfe)) {
            throw new UnauthorizedException("AccÃ¨s non autorisÃ© Ã  cette conversation");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messages = messageRepository.findConversation(pfeId, userId, otherUserId, pageable);

        List<MessageDTO> enriched = messages.getContent().stream()
                .map(msg -> enrichWithUserNames(messageMapper.toDTO(msg)))
                .collect(Collectors.toList());

        return PageResponse.<MessageDTO>builder()
                .content(enriched)
                .pageNumber(messages.getNumber())
                .pageSize(messages.getSize())
                .totalElements(messages.getTotalElements())
                .totalPages(messages.getTotalPages())
                .last(messages.isLast())
                .first(messages.isFirst())
                .build();
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getUnreadMessages(Long userId) {
        List<Message> unread = messageRepository.findUnreadByReceiver(userId);
        return unread.stream()
                .map(msg -> enrichWithUserNames(messageMapper.toDTO(msg)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return messageRepository.countUnreadByReceiver(userId);
    }

    public void markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message non trouvÃ©"));

        if (!message.getReceiverId().equals(userId)) {
            throw new UnauthorizedException("Vous ne pouvez pas marquer ce message comme lu");
        }

        message.setStatus(Message.MessageStatus.READ);
        message.setReadAt(LocalDateTime.now());
        messageRepository.save(message);
        log.info("Message {} marked as read by user {}", messageId, userId);
    }

    public void markMultipleAsRead(List<Long> messageIds, Long userId) {
        messageRepository.markAsRead(messageIds, userId, LocalDateTime.now());
        log.info("Messages {} marked as read by user {}", messageIds, userId);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesByPfe(Long pfeId, Long userId) {
        PFE pfe = pfeRepository.findById(pfeId)
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvé"));

        if (!isParticipantInPFE(userId, pfe)) {
            throw new UnauthorizedException("Accès non autorisé");
        }

        return messageRepository.findByPfeId(pfeId).stream()
                .map(msg -> enrichWithUserNames(messageMapper.toDTO(msg)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageDTO> searchConversation(Long pfeId, Long userId, Long otherUserId, String keyword, int page, int size) {
        PFE pfe = pfeRepository.findById(pfeId)
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvé"));

        if (!isParticipantInPFE(userId, pfe)) {
            throw new UnauthorizedException("Accès non autorisé à cette conversation");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messages = messageRepository.searchConversation(pfeId, userId, otherUserId, keyword, pageable);

        List<MessageDTO> enriched = messages.getContent().stream()
                .map(msg -> enrichWithUserNames(messageMapper.toDTO(msg)))
                .collect(Collectors.toList());

        return PageResponse.<MessageDTO>builder()
                .content(enriched)
                .pageNumber(messages.getNumber())
                .pageSize(messages.getSize())
                .totalElements(messages.getTotalElements())
                .totalPages(messages.getTotalPages())
                .last(messages.isLast())
                .first(messages.isFirst())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageDTO> getConversationByDateRange(Long pfeId, Long userId, Long otherUserId, 
                                                               LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        PFE pfe = pfeRepository.findById(pfeId)
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvé"));

        if (!isParticipantInPFE(userId, pfe)) {
            throw new UnauthorizedException("Accès non autorisé à cette conversation");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messages = messageRepository.findConversationByDateRange(pfeId, userId, otherUserId, startDate, endDate, pageable);

        List<MessageDTO> enriched = messages.getContent().stream()
                .map(msg -> enrichWithUserNames(messageMapper.toDTO(msg)))
                .collect(Collectors.toList());

        return PageResponse.<MessageDTO>builder()
                .content(enriched)
                .pageNumber(messages.getNumber())
                .pageSize(messages.getSize())
                .totalElements(messages.getTotalElements())
                .totalPages(messages.getTotalPages())
                .last(messages.isLast())
                .first(messages.isFirst())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageDTO> searchByKeywordInPfe(Long pfeId, Long userId, String keyword, int page, int size) {
        PFE pfe = pfeRepository.findById(pfeId)
                .orElseThrow(() -> new ResourceNotFoundException("PFE non trouvé"));

        if (!isParticipantInPFE(userId, pfe)) {
            throw new UnauthorizedException("Accès non autorisé");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messages = messageRepository.searchByKeywordInPfe(pfeId, keyword, pageable);

        List<MessageDTO> enriched = messages.getContent().stream()
                .map(msg -> enrichWithUserNames(messageMapper.toDTO(msg)))
                .collect(Collectors.toList());

        return PageResponse.<MessageDTO>builder()
                .content(enriched)
                .pageNumber(messages.getNumber())
                .pageSize(messages.getSize())
                .totalElements(messages.getTotalElements())
                .totalPages(messages.getTotalPages())
                .last(messages.isLast())
                .first(messages.isFirst())
                .build();
    }

    private boolean isParticipantInPFE(Long userId, PFE pfe) {
        return pfe.getStudentId().equals(userId) ||
               (pfe.getSupervisorId() != null && pfe.getSupervisorId().equals(userId));
    }

    private MessageDTO enrichWithUserNames(MessageDTO dto) {
        userRepository.findById(dto.getSenderId())
                .ifPresent(user -> dto.setSenderName(user.getFullName()));
        userRepository.findById(dto.getReceiverId())
                .ifPresent(user -> dto.setReceiverName(user.getFullName()));
        return dto;
    }
}

