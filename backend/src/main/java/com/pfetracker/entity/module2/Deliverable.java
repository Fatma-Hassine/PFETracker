package com.pfetracker.entity.module2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Module2Deliverable")
@Table(name = "module2_deliverables")
public class Deliverable extends BaseModule2Entity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @JsonIgnore
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    @JsonIgnore
    private Milestone milestone;

    private String originalFileName;

    private String storedFileName;

    private String contentType;

    private Long sizeBytes;

    private Integer versionNumber;

    private String filePath;

    private Long uploadedByUserId;
}