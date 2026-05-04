package com.pfetracker.mapper.module1;
import com.pfetracker.dto.module1.EtudiantResponse;
import com.pfetracker.dto.module1.ProfilUpdateRequest;
import com.pfetracker.entity.module1.Etudiant;
import org.springframework.stereotype.Component;

@Component("etudiantMapperM1")

public class EtudiantMapper {

    public EtudiantResponse toResponse(Etudiant e) {
        return EtudiantResponse.builder()
                .id(e.getId())
                .nomComplet(e.getNomComplet())
                .email(e.getEmail())
                .telephone(e.getTelephone())
                .niveauEtudes(e.getNiveauEtudes())
                .annee(e.getAnnee())
                .departementNom(
                    e.getDepartement() != null
                        ? e.getDepartement().getNom()
                        : null)
                .encadrantNom(
                    e.getEncadrant() != null
                        ? e.getEncadrant().getNomComplet()
                        : null)
                .enabled(e.isEnabled())
                .build();
    }

    public void updateFromRequest(ProfilUpdateRequest req, Etudiant e) {
        if (req.getNomComplet()  != null) e.setNomComplet(req.getNomComplet());
        if (req.getTelephone()   != null) e.setTelephone(req.getTelephone());
    }
}
