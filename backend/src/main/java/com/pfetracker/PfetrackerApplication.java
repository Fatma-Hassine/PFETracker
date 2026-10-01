package com.pfetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class PfetrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PfetrackerApplication.class, args);
    }

    private static void createLogFileIfNotExists() {
        try {
            Path logFilePath = Paths.get("../logs/pfetracker.log").toAbsolutePath().normalize();
            Path logDirectory = logFilePath.getParent();

            if (logDirectory != null && !Files.exists(logDirectory)) {
                Files.createDirectories(logDirectory);
            }

            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
            }

        } catch (IOException e) {
            System.err.println("Impossible de créer le fichier de log : " + e.getMessage());
        }
    }
}
