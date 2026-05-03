package com.pfetracker.repository.module1;
import com.pfetracker.entity.module1.ChefDepartement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface ChefDepartementRepository extends JpaRepository<ChefDepartement, Long>{
    Optional<ChefDepartement> findByDepartementId(Long departementId);

}
