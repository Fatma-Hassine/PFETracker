package com.pfetracker.repository.module2;

import com.pfetracker.entity.module2.ProgressSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressSnapshotRepository extends JpaRepository<ProgressSnapshot, Long> {

    List<ProgressSnapshot> findByPfeIdOrderByCreatedAtAsc(Long pfeId);
}