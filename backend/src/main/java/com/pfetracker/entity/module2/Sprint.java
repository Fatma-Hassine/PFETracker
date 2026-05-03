package com.pfetracker.entity.module2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfetracker.entity.module2.enums.SprintStatus;
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

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Module2Sprint")
@Table(name = "module2_sprints")
public class Sprint extends BaseModule2Entity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pfe_id", nullable = false)
    @JsonIgnore
    private Pfe pfe;

    @Column(columnDefinition = "TEXT")
    private String objective;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String studentReport;

    @Column(columnDefinition = "TEXT")
    private String supervisorComment;

    @Enumerated(EnumType.STRING)
    private SprintStatus status = SprintStatus.PLANNED;
}