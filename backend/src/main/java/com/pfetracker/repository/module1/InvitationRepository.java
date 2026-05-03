package com.pfetracker.repository.module1;
import java.util.List;
import java.util.Optional;
import com.pfetracker.entity.module1.Invitation;
import com.pfetracker.entity.module1.enums.StatutInvitation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository

public interface InvitationRepository extends JpaRepository<Invitation, Long>{
	 Optional<Invitation> findByToken(String token);

	    List<Invitation> findByEncadrantIdOrderByDateExpirationDesc(Long encadrantId);

	    List<Invitation> findByEtudiantId(Long etudiantId);

	    boolean existsByEncadrantIdAndEtudiantIdAndStatut(
	            Long encadrantId, Long etudiantId, StatutInvitation statut);
}
