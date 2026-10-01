package com.pfetracker.repository.module2;

import com.pfetracker.entity.module2.Task;
import com.pfetracker.entity.module2.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface Module2TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByMilestoneId(Long milestoneId);

    List<Task> findByMilestonePfeId(Long pfeId);

    List<Task> findByDeadlineBeforeAndStatusNot(LocalDate date, TaskStatus status);

    List<Task> findByMilestonePfeIdAndUpdatedAtAfter(Long pfeId, LocalDateTime dateTime);

    @Query("SELECT t FROM Module2Task t WHERE t.milestone.pfe.id IN :pfeIds AND t.status = "
            + "com.pfetracker.entity.module2.enums.TaskStatus.SUBMITTED")
    List<Task> findPendingValidations(@Param("pfeIds") List<Long> pfeIds);
}