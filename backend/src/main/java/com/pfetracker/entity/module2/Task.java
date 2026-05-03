package com.pfetracker.entity.module2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfetracker.entity.module2.enums.Priority;
import com.pfetracker.entity.module2.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Module2Task")
@Table(name = "module2_tasks")
public class Task extends BaseModule2Entity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id", nullable = false)
    @JsonIgnore
    private Milestone milestone;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.NOT_STARTED;

    private Integer estimatedHours;

    private Double progress = 0.0;

    private Long assignedStudentId;

    private Long createdByUserId;

    private String createdByRole;

    @Column(columnDefinition = "TEXT")
    private String correctionNote;

    @JsonIgnore
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTask> subTasks = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskHistory> histories = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deliverable> deliverables = new ArrayList<>();
}