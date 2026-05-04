package com.pfetracker.repository.module1;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfetracker.entity.module1.ResponsableDepartement;

@Repository

public interface ResponsableDepartementRepository extends JpaRepository<ResponsableDepartement, Long>{
    Optional<ResponsableDepartement> findByDepartementId(Long departementId);

}
