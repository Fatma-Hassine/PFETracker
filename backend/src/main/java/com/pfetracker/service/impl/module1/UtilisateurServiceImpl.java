package com.pfetracker.service.impl.module1;

import org.springframework.stereotype.Service;

import com.pfetracker.dto.module1.ProfilUpdateRequest;
import com.pfetracker.dto.module1.UtilisateurResponse;
import com.pfetracker.entity.module1.Encadrant;
import com.pfetracker.entity.module1.Etudiant;
import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.exception.module1.BusinessException;
import com.pfetracker.repository.module1.UtilisateurRepository;
import com.pfetracker.service.module1.UtilisateurService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService{
	private final UtilisateurRepository utilisateurRepo;

    @Override
    public UtilisateurResponse getById(Long id) {
        Utilisateur user = utilisateurRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));
        return UtilisateurResponse.fromEntity(user);
    }

    @Override
    public UtilisateurResponse getByEmail(String email) {
        Utilisateur user = utilisateurRepo.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));
        return UtilisateurResponse.fromEntity(user);
    }

    @Override
    public UtilisateurResponse mettreAJourProfil(Long id, ProfilUpdateRequest req) {
        Utilisateur user = utilisateurRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        // Email et département modifiables uniquement par l'admin — pas ici
        if (req.getNomComplet() != null) user.setNomComplet(req.getNomComplet());

        if (user instanceof Etudiant e) {
            if (req.getTelephone() != null) e.setTelephone(req.getTelephone());
        } else if (user instanceof Encadrant enc) {
            if (req.getGrade() != null) enc.setGrade(req.getGrade());
            if (req.getSpecialite() != null) enc.setSpecialite(req.getSpecialite());
            if (req.getBureau() != null) enc.setBureau(req.getBureau());
        }

        utilisateurRepo.save(user);
        return UtilisateurResponse.fromEntity(user);
    }

    
}
