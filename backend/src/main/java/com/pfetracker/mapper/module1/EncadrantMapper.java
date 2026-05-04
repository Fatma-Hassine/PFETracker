package com.pfetracker.mapper.module1;
import com.pfetracker.dto.module1.EncadrantResponse;
import com.pfetracker.dto.module1.ProfilUpdateRequest;
import com.pfetracker.entity.module1.Encadrant;
import org.springframework.stereotype.Component;

@Component("encadrantMapperM1")

public class EncadrantMapper {
	public EncadrantResponse toResponse(Encadrant e) {
        return EncadrantResponse.builder()
                .id(e.getId())
                .nomComplet(e.getNomComplet())
                .email(e.getEmail())
                .grade(e.getGrade())
                .specialite(e.getSpecialite())
                .bureau(e.getBureau())
                .codeId(e.getCodeId())
                .departementNom(
                    e.getDepartement() != null
                        ? e.getDepartement().getNom()
                        : null)
                .enabled(e.isEnabled())
                .build();
    }

    public void updateFromRequest(ProfilUpdateRequest req, Encadrant e) {
        if (req.getNomComplet()  != null) e.setNomComplet(req.getNomComplet());
        if (req.getGrade()       != null) e.setGrade(req.getGrade());
        if (req.getSpecialite()  != null) e.setSpecialite(req.getSpecialite());
        if (req.getBureau()      != null) e.setBureau(req.getBureau());
    }
}
