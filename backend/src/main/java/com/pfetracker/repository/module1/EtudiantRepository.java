package com.pfetracker.repository.module1;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pfetracker.entity.module1.Etudiant;
@Repository

public interface EtudiantRepository extends JpaRepository<Etudiant, Long>{
	 List<Etudiant> findByDepartementId(Long departementId);

	    List<Etudiant> findByEncadrantIsNull();

	    List<Etudiant> findByDepartementIdAndEncadrantIsNull(Long departementId);

	    long countByDepartementId(Long departementId);
	    long countByEncadrantIsNotNull();
	    long countByDepartementIdAndEncadrantIsNull(Long departementId);

	    long countByEncadrantId(Long encadrantId);
	    long countByEncadrantIsNull();
	    
	    @Query("SELECT e FROM Etudiant e WHERE e.encadrant IS NULL " +
	            "AND e.dateDebutStage IS NOT NULL " +
	            "AND e.dateDebutStage <= :dateLimite")
	     List<Etudiant> findNonAffectesDepuis(LocalDate dateLimite);

	     // Stages qui expirent avant une date donnée
	     @Query("SELECT e FROM Etudiant e WHERE e.dateFinStage IS NOT NULL " +
	            "AND e.dateFinStage <= :dateLimite " +
	            "AND e.dateFinStage >= CURRENT_DATE")
	     List<Etudiant> findStagesProchesExpiration(LocalDate dateLimite);

	     // Stages déjà expirés
	     @Query("SELECT e FROM Etudiant e WHERE e.dateFinStage IS NOT NULL " +
	            "AND e.dateFinStage < CURRENT_DATE")
	     List<Etudiant> findStagesExpires();
}
