package com.pfetracker.security.module1;

import org.springframework.security.core.userdetails.UserDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.pfetracker.repository.module1.UtilisateurRepository;

@Service("customUserDetailsServiceM1")

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	private final UtilisateurRepository utilisateurRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return utilisateurRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Utilisateur introuvable : " + email));
    }
}
