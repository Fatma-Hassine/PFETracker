package com.pfetracker.repository.module3;

import com.pfetracker.entity.module3.ArchivedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivedMessageRepository extends JpaRepository<ArchivedMessage, Long> {

    @Query("SELECT am FROM ArchivedMessage am WHERE am.pfeId = :pfeId ORDER BY am.originalCreatedAt DESC")
    List<ArchivedMessage> findByPfeId(@Param("pfeId") Long pfeId);

    @Query("SELECT COUNT(am) FROM ArchivedMessage am WHERE am.pfeId = :pfeId")
    Long countByPfeId(@Param("pfeId") Long pfeId);

    @Query("SELECT am FROM ArchivedMessage am WHERE am.archiveReason = :reason")
    List<ArchivedMessage> findByArchiveReason(@Param("reason") String reason);
}
