package com.pfetracker.scheduler.module3;

import com.pfetracker.entity.module3.ArchivedMessage;
import com.pfetracker.entity.module3.Message;
import com.pfetracker.entity.module3.PFE;
import com.pfetracker.repository.module3.ArchivedMessageRepository;
import com.pfetracker.repository.module3.MessageRepository;
import com.pfetracker.repository.module3.PFERepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageArchivingScheduler {

    private final MessageRepository messageRepository;
    private final ArchivedMessageRepository archivedMessageRepository;
    private final PFERepository pfeRepository;

    /**
     * Archive messages when PFE is closed
     * Runs every day at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void archiveClosedPFEMessages() {
        log.info("Starting message archiving for closed PFEs");

        try {
            // Find all closed PFEs
            List<PFE> closedPFEs = pfeRepository.findAll().stream()
                    .filter(pfe -> pfe.getStatus().equals(PFE.PFEStatus.COMPLETED) ||
                                  pfe.getStatus().equals(PFE.PFEStatus.SUSPENDED) ||
                                  pfe.getStatus().equals(PFE.PFEStatus.DEFENDED))
                    .collect(Collectors.toList());

            for (PFE pfe : closedPFEs) {
                archiveMessagesForPFE(pfe.getId(), "PFE_CLOSED");
            }

            log.info("Message archiving completed for {} closed PFEs", closedPFEs.size());
        } catch (Exception e) {
            log.error("Error during message archiving", e);
        }
    }

    /**
     * Archive messages older than specified days (configurable)
     * Runs every Sunday at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * 0")
    @Transactional
    public void archiveOldMessages() {
        log.info("Starting archiving of old messages");

        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
            List<Message> oldMessages = messageRepository.findAll().stream()
                    .filter(msg -> msg.getCreatedAt().isBefore(cutoffDate))
                    .collect(Collectors.toList());

            for (Message msg : oldMessages) {
                archiveMessage(msg, "OLD_MESSAGE");
            }

            log.info("Archived {} old messages", oldMessages.size());
        } catch (Exception e) {
            log.error("Error archiving old messages", e);
        }
    }

    /**
     * Archive all messages for a specific PFE
     */
    @Transactional
    public void archiveMessagesForPFE(Long pfeId, String archiveReason) {
        List<Message> pfeMessages = messageRepository.findByPfeId(pfeId);

        List<ArchivedMessage> archivedMessages = pfeMessages.stream()
                .map(msg -> ArchivedMessage.builder()
                        .originalMessageId(msg.getId())
                        .pfeId(msg.getPfeId())
                        .senderId(msg.getSenderId())
                        .receiverId(msg.getReceiverId())
                        .content(msg.getContent())
                        .originalCreatedAt(msg.getCreatedAt())
                        .archiveReason(archiveReason)
                        .attachmentUrl(msg.getAttachmentUrl())
                        .attachmentName(msg.getAttachmentName())
                        .build())
                .collect(Collectors.toList());

        archivedMessageRepository.saveAll(archivedMessages);
        log.info("Archived {} messages for PFE {}", archivedMessages.size(), pfeId);
    }

    /**
     * Archive a single message
     */
    @Transactional
    public void archiveMessage(Message message, String archiveReason) {
        ArchivedMessage archived = ArchivedMessage.builder()
                .originalMessageId(message.getId())
                .pfeId(message.getPfeId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .originalCreatedAt(message.getCreatedAt())
                .archiveReason(archiveReason)
                .attachmentUrl(message.getAttachmentUrl())
                .attachmentName(message.getAttachmentName())
                .build();

        archivedMessageRepository.save(archived);
    }
}
