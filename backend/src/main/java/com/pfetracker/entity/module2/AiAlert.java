package com.pfetracker.entity.module2;

import com.pfetracker.entity.module2.enums.AlertSeverity;
import com.pfetracker.entity.module2.enums.AlertType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Module2AiAlert")
@Table(name = "module2_ai_alerts")
public class AiAlert extends BaseModule2Entity {

    private Long pfeId;

    private Long studentId;

    private Long supervisorId;

    @Enumerated(EnumType.STRING)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String referenceType;

    private Long referenceId;

    private Boolean resolved = false;
}