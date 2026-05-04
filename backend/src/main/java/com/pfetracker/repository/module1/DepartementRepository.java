package com.pfetracker.repository.module1;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfetracker.entity.module1.Departement;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {
	Optional<Departement> findByCode(String code);

    Optional<Departement> findByNom(String nom);

    boolean existsByCode(String code);
}
