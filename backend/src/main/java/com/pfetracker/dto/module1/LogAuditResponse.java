package com.pfetracker.dto.module1;
import lombok.*;
import java.time.LocalDateTime;

import com.pfetracker.entity.module1.LogAudit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogAuditResponse {
	private Long id;
    private String typeAction;
    private LocalDateTime horodatage;
    private String adresseIp;
    private String resultat;
    private String details;
    private String utilisateurEmail; 

    public static LogAuditResponse fromEntity(LogAudit log) {
        return LogAuditResponse.builder()
                .id(log.getId())
                .typeAction(log.getTypeAction())
                .horodatage(log.getHorodatage())
                .adresseIp(log.getAdresseIp())
                .resultat(log.getResultat())
                .details(log.getDetails())
                .utilisateurEmail(log.getUtilisateur() != null
                        ? log.getUtilisateur().getEmail() : "Système")
                .build();
    }
}
