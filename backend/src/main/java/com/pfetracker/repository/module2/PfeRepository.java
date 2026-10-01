package com.pfetracker.repository.module2;

import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.enums.PfeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PfeRepository extends JpaRepository<Pfe, Long> {

    List<Pfe> findByStudentId(Long studentId);

    Optional<Pfe> findFirstByStudentId(Long studentId);

    List<Pfe> findBySupervisorId(Long supervisorId);

    List<Pfe> findByStatus(PfeStatus status);

    @Query("SELECT p FROM Module2Pfe p WHERE p.supervisorId = :supervisorId "
            + "AND p.status IN (com.pfetracker.entity.module2.enums.PfeStatus.INITIALIZED, "
            + "com.pfetracker.entity.module2.enums.PfeStatus.IN_PROGRESS, "
            + "com.pfetracker.entity.module2.enums.PfeStatus.LATE)")
    List<Pfe> findActiveBySupervisorId(@Param("supervisorId") Long supervisorId);
}