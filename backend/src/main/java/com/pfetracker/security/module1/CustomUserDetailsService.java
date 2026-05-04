package com.pfetracker.security.module1;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.repository.module1.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service("customUserDetailsServiceM1")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // MODIF : on récupère l'entité Utilisateur
        Utilisateur utilisateur = utilisateurRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur introuvable : " + email));

        // MODIF : on construit un UserDetails Spring Security
        // au lieu de retourner directement l'entité
        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .authorities(List.of(
                        new SimpleGrantedAuthority(utilisateur.getRole().name())
                ))
                .accountLocked(utilisateur.isAccountLocked())
                // MODIF : disabled(false) → permet aux comptes enabled=false
                // de s'authentifier pour changer leur mot de passe temporaire
                .disabled(false)
                .build();
    }
}