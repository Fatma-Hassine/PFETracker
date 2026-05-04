package com.pfetracker.entity.module2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfetracker.entity.module2.enums.MilestoneStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Entity(name = "Module2Milestone")
@Table(name = "module2_milestones")
public class Milestone extends BaseModule2Entity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pfe_id", nullable = false)
    @JsonIgnore
    private Pfe pfe;

    private Integer orderIndex;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String expectedDeliverable;

    private LocalDate plannedStartDate;

    private LocalDate plannedEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    @Enumerated(EnumType.STRING)
    private MilestoneStatus status = MilestoneStatus.NOT_STARTED;

    private Double progress = 0.0;

    private Double weight = 1.0;

    @Column(columnDefinition = "TEXT")
    private String supervisorComment;

    @JsonIgnore
    @OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
}