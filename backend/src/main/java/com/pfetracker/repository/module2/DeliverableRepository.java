package com.pfetracker.repository.module2;

import com.pfetracker.entity.module2.Deliverable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliverableRepository extends JpaRepository<Deliverable, Long> {

    List<Deliverable> findByTaskIdOrderByVersionNumberDesc(Long taskId);

    List<Deliverable> findByMilestoneIdOrderByVersionNumberDesc(Long milestoneId);

    int countByTaskId(Long taskId);

    int countByMilestoneId(Long milestoneId);
}