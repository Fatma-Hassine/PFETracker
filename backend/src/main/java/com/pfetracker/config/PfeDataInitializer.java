package com.pfetracker.config;

import com.pfetracker.dto.module2.PfeImportResultDTO;
import com.pfetracker.service.module2.PfeImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PfeDataInitializer implements CommandLineRunner {

    private final PfeImportService pfeImportService;

    @Value("${pfe.import.auto-enabled:true}")
    private boolean autoImportEnabled;

    @Value("${pfe.import.file:data/affectations_pfe.xlsx}")
    private String importFilePath;

    @Override
    public void run(String... args) {
        if (!autoImportEnabled) {
            System.out.println("Import automatique PFE désactivé.");
            return;
        }

        try {
            PfeImportResultDTO result = pfeImportService.importAffectationsFromClasspath(importFilePath);

            System.out.println("======================================");
            System.out.println("Import automatique des PFE terminé");
            System.out.println("Fichier : " + importFilePath);
            System.out.println("Lignes lues : " + result.getTotalRows());
            System.out.println("Créés : " + result.getCreatedCount());
            System.out.println("Ignorés : " + result.getSkippedCount());
            System.out.println("Erreurs : " + result.getErrorCount());

            if (result.getErrorCount() > 0) {
                System.out.println("Détails des erreurs :");
                result.getMessages()
                        .stream()
                        .filter(message -> message.contains("erreur"))
                        .forEach(System.out::println);
            }

            System.out.println("======================================");
        } catch (Exception exception) {
            System.err.println("Import automatique PFE ignoré : " + exception.getMessage());
        }
    }
}
