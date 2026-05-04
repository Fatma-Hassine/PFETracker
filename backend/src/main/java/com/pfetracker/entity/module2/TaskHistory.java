package com.pfetracker.entity.module2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfetracker.entity.module2.enums.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Entity(name = "Module2TaskHistory")
@Table(name = "module2_task_histories")
public class TaskHistory extends BaseModule2Entity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore
    private Task task;

    @Enumerated(EnumType.STRING)
    private TaskStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private TaskStatus newStatus;

    private Long changedByUserId;

    private String changedByRole;

    @Column(columnDefinition = "TEXT")
    private String comment;
}