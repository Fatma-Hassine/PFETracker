package com.pfetracker.repository.module3;

import com.pfetracker.entity.module3.Message;
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
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.pfeId = :pfeId AND " +
           "((m.senderId = :user1 AND m.receiverId = :user2) OR " +
           "(m.senderId = :user2 AND m.receiverId = :user1)) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversation(
            @Param("pfeId") Long pfeId,
            @Param("user1") Long user1,
            @Param("user2") Long user2,
            Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.receiverId = :userId AND m.status = 'UNREAD' ORDER BY m.createdAt DESC")
    List<Message> findUnreadByReceiver(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :userId AND m.status = 'UNREAD'")
    Long countUnreadByReceiver(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Message m SET m.status = 'READ', m.readAt = :now WHERE m.id IN :ids AND m.receiverId = :userId")
    void markAsRead(@Param("ids") List<Long> ids, @Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT m FROM Message m WHERE m.pfeId = :pfeId ORDER BY m.createdAt DESC")
    List<Message> findByPfeId(@Param("pfeId") Long pfeId);

    @Query("SELECT DISTINCT m.pfeId FROM Message m WHERE m.senderId = :userId OR m.receiverId = :userId")
    List<Long> findDistinctPfeIdsByUser(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE m.pfeId = :pfeId AND " +
           "((m.senderId = :user1 AND m.receiverId = :user2) OR " +
           "(m.senderId = :user2 AND m.receiverId = :user1)) AND " +
           "m.content LIKE %:keyword% " +
           "ORDER BY m.createdAt DESC")
    Page<Message> searchConversation(
            @Param("pfeId") Long pfeId,
            @Param("user1") Long user1,
            @Param("user2") Long user2,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.pfeId = :pfeId AND " +
           "((m.senderId = :user1 AND m.receiverId = :user2) OR " +
           "(m.senderId = :user2 AND m.receiverId = :user1)) AND " +
           "m.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversationByDateRange(
            @Param("pfeId") Long pfeId,
            @Param("user1") Long user1,
            @Param("user2") Long user2,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.pfeId = :pfeId AND " +
           "((m.senderId = :user1 AND m.receiverId = :user2) OR " +
           "(m.senderId = :user2 AND m.receiverId = :user1)) AND " +
           "m.content LIKE %:keyword% AND " +
           "m.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY m.createdAt DESC")
    Page<Message> searchConversationByDateRange(
            @Param("pfeId") Long pfeId,
            @Param("user1") Long user1,
            @Param("user2") Long user2,
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.pfeId = :pfeId AND m.content LIKE %:keyword% " +
           "ORDER BY m.createdAt DESC")
    Page<Message> searchByKeywordInPfe(@Param("pfeId") Long pfeId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.pfeId = :pfeId AND " +
           "m.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findByPfeIdAndDateRange(
            @Param("pfeId") Long pfeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}

