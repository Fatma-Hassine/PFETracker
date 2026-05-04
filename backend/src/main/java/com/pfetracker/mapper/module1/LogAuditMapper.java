package com.pfetracker.mapper.module1;
import com.pfetracker.dto.module1.LogAuditResponse;
import com.pfetracker.entity.module1.LogAudit;
import com.pfetracker.dto.module1.LogAuditResponse;
import com.pfetracker.entity.module1.LogAudit;
import org.springframework.stereotype.Component;

@Component("logAuditMapperM1")
public class LogAuditMapper {
	public LogAuditResponse toResponse(LogAudit log) {
        return LogAuditResponse.builder()
                .id(log.getId())
                .typeAction(log.getTypeAction())
                .horodatage(log.getHorodatage())
                .adresseIp(log.getAdresseIp())
                .resultat(log.getResultat())
                .details(log.getDetails())
                .utilisateurEmail(
                    log.getUtilisateur() != null
                        ? log.getUtilisateur().getEmail()
                        : "Système")
                .build();
    }
}
