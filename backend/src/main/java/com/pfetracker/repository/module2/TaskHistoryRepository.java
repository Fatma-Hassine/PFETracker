package com.pfetracker.repository.module2;

import com.pfetracker.entity.module2.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    List<TaskHistory> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}