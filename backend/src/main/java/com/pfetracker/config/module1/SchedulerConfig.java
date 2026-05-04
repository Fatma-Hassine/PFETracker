package com.pfetracker.config.module1;
import com.pfetracker.repository.*;
import com.pfetracker.repository.module1.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Configuration("schedulerConfigM1")
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {
	private final RefreshTokenRepository refreshTokenRepo;
    private final PasswordResetTokenRepository resetTokenRepo;
    private final LogAuditRepository logAuditRepo;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void nettoyerRefreshTokens() {
        log.info("Nettoyage des refresh tokens expirés...");
        refreshTokenRepo.supprimerTokensInvalides();
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void nettoyerResetTokens() {
        log.info("Nettoyage des tokens de réinitialisation expirés...");
        resetTokenRepo.supprimerTokensExpires();
    }

    @Scheduled(cron = "0 0 2 1 * *")
    @Transactional
    public void nettoyerLogsAnciens() {
        LocalDateTime limite = LocalDateTime.now().minusMonths(6);
        log.info("Suppression des logs antérieurs à {}...", limite);
        logAuditRepo.supprimerLogsAnciens(limite);
    }
}
