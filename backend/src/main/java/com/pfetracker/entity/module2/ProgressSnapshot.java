package com.pfetracker.entity.module2;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Module2ProgressSnapshot")
@Table(name = "module2_progress_snapshots")
public class ProgressSnapshot extends BaseModule2Entity {

    private Long pfeId;

    private Double progress;
}