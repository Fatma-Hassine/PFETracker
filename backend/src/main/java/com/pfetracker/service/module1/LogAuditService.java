package com.pfetracker.service.module1;

import com.pfetracker.dto.module1.LogAuditResponse;
import com.pfetracker.entity.module1.Utilisateur;
import java.util.List;

import org.springframework.stereotype.Service;
@Service("logAuditServiceM1")

public interface LogAuditService {
	void log(Utilisateur utilisateur, String typeAction,
            String adresseIp, String resultat, String details);
   List<LogAuditResponse> getLogsDepartement(Long departementId);
   List<LogAuditResponse> getLogsUtilisateur(Long userId);

}
