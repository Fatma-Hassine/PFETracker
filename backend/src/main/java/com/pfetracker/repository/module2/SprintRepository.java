package com.pfetracker.repository.module2;

import com.pfetracker.entity.module2.Sprint;
import com.pfetracker.entity.module2.enums.SprintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SprintRepository extends JpaRepository<Sprint, Long> {

    List<Sprint> findByPfeIdOrderByStartDateDesc(Long pfeId);

    List<Sprint> findByEndDateBeforeAndStatusNot(LocalDate date, SprintStatus status);

    List<Sprint> findByStatusAndUpdatedAtBefore(SprintStatus status, LocalDateTime dateTime);
}