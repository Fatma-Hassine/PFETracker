package com.pfetracker.repository.module3;

import com.pfetracker.entity.module3.NotificationM3;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepositoryM3 extends JpaRepository<NotificationM3, Long> {

    @Query("SELECT n FROM Module3Notification  n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    Page<NotificationM3> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT n FROM Module3Notification  n WHERE n.userId = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<NotificationM3> findUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Module3Notification  n WHERE n.userId = :userId AND n.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Module3Notification  n SET n.isRead = true, n.readAt = :now WHERE n.id IN :ids AND n.userId = :userId")
    void markAsRead(@Param("ids") List<Long> ids, @Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Module3Notification  n SET n.isRead = true, n.readAt = :now WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT n FROM Module3Notification  n WHERE n.userId = :userId AND n.type = :type ORDER BY n.createdAt DESC")
    List<NotificationM3> findByUserIdAndType(@Param("userId") Long userId, @Param("type") NotificationM3.NotificationType type);

    @Query("SELECT n FROM Module3Notification  n WHERE n.sentByEmail = false AND n.createdAt < :before ORDER BY n.createdAt ASC")
    List<NotificationM3> findUnsentEmails(@Param("before") LocalDateTime before);
}

