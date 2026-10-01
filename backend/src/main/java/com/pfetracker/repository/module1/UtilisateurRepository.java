package com.pfetracker.repository.module1;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.entity.module1.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>{
	 Optional<Utilisateur> findByEmail(String email);

	    boolean existsByEmail(String email);

	    List<Utilisateur> findByRole(Role role);

	    List<Utilisateur> findByEnabledFalseAndRole(Role role);
	    List<Utilisateur> findByAccountLockedTrue();
	    long countByAccountLockedTrue();
	    List<Utilisateur> findByIdIn(List<Long> ids);
}
