package com.pfetracker.repository.module2;

import com.pfetracker.entity.module2.AiAlert;
import com.pfetracker.entity.module2.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiAlertRepository extends JpaRepository<AiAlert, Long> {

    List<AiAlert> findByPfeIdAndResolvedFalseOrderByCreatedAtDesc(Long pfeId);

    boolean existsByPfeIdAndTypeAndReferenceTypeAndReferenceIdAndResolvedFalse(
            Long pfeId,
            AlertType type,
            String referenceType,
            Long referenceId
    );
}