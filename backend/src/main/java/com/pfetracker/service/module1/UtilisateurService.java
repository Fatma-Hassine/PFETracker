package com.pfetracker.service.module1;

import org.springframework.stereotype.Service;

import com.pfetracker.dto.module1.*;
@Service("utilisateurServiceM1")

public interface UtilisateurService {
	UtilisateurResponse getById(Long id);
    UtilisateurResponse getByEmail(String email);
    UtilisateurResponse mettreAJourProfil(Long id, ProfilUpdateRequest request);

}
