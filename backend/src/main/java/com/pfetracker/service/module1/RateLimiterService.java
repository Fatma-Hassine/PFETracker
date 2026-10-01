package com.pfetracker.service.module1;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiting en mémoire, sans dépendance externe (même philosophie que le
 * moteur de détection de retard du Module 2 : Java pur + Spring Scheduler).
 * Fenêtre fixe d'une minute par clé (typiquement IP + endpoint).
 */
@Service
public class RateLimiterService {

    private static final long FENETRE_MS = 60_000L;

    private record Compteur(AtomicInteger nombre, Instant debutFenetre) {}

    private final ConcurrentHashMap<String, Compteur> compteurs = new ConcurrentHashMap<>();

    /** @return true si la requête est autorisée, false si la limite est dépassée. */
    public boolean isAllowed(String cle, int maxParMinute) {
        Instant maintenant = Instant.now();

        Compteur compteur = compteurs.compute(cle, (k, existant) -> {
            if (existant == null || maintenant.toEpochMilli() - existant.debutFenetre().toEpochMilli() >= FENETRE_MS) {
                return new Compteur(new AtomicInteger(1), maintenant);
            }
            existant.nombre().incrementAndGet();
            return existant;
        });

        return compteur.nombre().get() <= maxParMinute;
    }

    /** Purge les fenêtres expirées — appelé périodiquement pour éviter une fuite mémoire. */
    public void nettoyerFenetresExpirees() {
        Instant maintenant = Instant.now();
        compteurs.entrySet().removeIf(entry ->
                maintenant.toEpochMilli() - entry.getValue().debutFenetre().toEpochMilli() >= FENETRE_MS
        );
    }
}
