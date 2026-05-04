package com.pfetracker.entity.module3;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "archived_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_message_id", nullable = false)
    private Long originalMessageId;

    @Column(name = "pfe_id", nullable = false)
    private Long pfeId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(name = "original_created_at", nullable = false)
    private LocalDateTime originalCreatedAt;

    @CreationTimestamp
    @Column(name = "archived_at", nullable = false, updatable = false)
    private LocalDateTime archivedAt;

    @Column(name = "archive_reason", length = 100)
    private String archiveReason; // e.g., "PFE_CLOSED", "USER_DELETED", "MANUAL"

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "attachment_name")
    private String attachmentName;
}
