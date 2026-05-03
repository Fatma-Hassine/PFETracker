package com.pfetracker.repository.module1;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.pfetracker.entity.module1.NotificationM1;

@Repository
public interface NotificationRepositoryM1 extends JpaRepository<NotificationM1, Long>{
    List<NotificationM1> findByDestinataireIdOrderByDateCreationDesc(Long userId);

    List<NotificationM1> findByDestinataireIdAndLuFalse(Long userId);

    long countByDestinataireIdAndLuFalse(Long userId);

    @Modifying
    @Query("UPDATE Module1Notification  n SET n.lu = true WHERE n.destinataire.id = :userId AND n.lu = false")
    void marquerToutesLues(Long userId);
}
