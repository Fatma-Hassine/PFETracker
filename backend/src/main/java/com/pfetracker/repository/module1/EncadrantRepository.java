package com.pfetracker.repository.module1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfetracker.entity.module1.Encadrant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EncadrantRepository extends JpaRepository<Encadrant, Long>{
	 List<Encadrant> findByDepartementId(Long departementId);

	    List<Encadrant> findByDepartementIdAndEnabledFalse(Long departementId);

	    Optional<Encadrant> findByCodeId(String codeId);

	    long countByDepartementId(Long departementId);
}
