package com.pfetracker.repository.module2;

import com.pfetracker.entity.module2.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findByPfeIdOrderByOrderIndexAsc(Long pfeId);
}