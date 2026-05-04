package com.pfetracker.entity.module2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfetracker.entity.module2.enums.PfeStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Entity(name = "Module2Pfe")
@Table(name = "module2_pfes")
public class Pfe extends BaseModule2Entity {

    private Long studentId;

    private String studentName;

    private String studentEmail;

    private Long supervisorId;

    private String supervisorName;

    private String supervisorEmail;

    private String department;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String problemStatement;

    @Column(columnDefinition = "TEXT")
    private String objectives;

    private String technologies;

    private LocalDate startDate;

    private LocalDate defenseDate;

    @Enumerated(EnumType.STRING)
    private PfeStatus status = PfeStatus.INITIALIZED;

    private Double progress = 0.0;

    @Column(columnDefinition = "TEXT")
    private String ficheValidationComment;

    @JsonIgnore
    @OneToMany(mappedBy = "pfe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Milestone> milestones = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "pfe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sprint> sprints = new ArrayList<>();
}