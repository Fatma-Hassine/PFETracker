package com.pfetracker.repository.module2;

import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.enums.PfeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PfeRepository extends JpaRepository<Pfe, Long> {

    List<Pfe> findByStudentId(Long studentId);

    List<Pfe> findBySupervisorId(Long supervisorId);

    List<Pfe> findByStatus(PfeStatus status);
}