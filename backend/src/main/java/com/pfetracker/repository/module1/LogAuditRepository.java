package com.pfetracker.repository.module1;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.pfetracker.entity.module1.LogAudit;

@Repository
public interface LogAuditRepository extends JpaRepository<LogAudit, Long>{
	 List<LogAudit> findByUtilisateurIdOrderByHorodatageDesc(Long userId);

	    List<LogAudit> findByUtilisateurIdAndTypeAction(Long userId, String typeAction);

	    // Logs entre deux dates pour un utilisateur
	    List<LogAudit> findByUtilisateurIdAndHorodatageBetween(
	            Long userId, LocalDateTime debut, LocalDateTime fin);

	    // Nettoyage des logs de plus de 6 mois 
	    @Modifying
	    @Query("DELETE FROM LogAudit l WHERE l.horodatage < :dateLimit")
	    void supprimerLogsAnciens(LocalDateTime dateLimit);
}
