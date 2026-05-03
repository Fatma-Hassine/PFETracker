package com.pfetracker.service.module2;

import com.pfetracker.dto.module2.AiAddGeneratedItemRequest;
import com.pfetracker.dto.module2.AiAddMultipleItemsRequest;
import com.pfetracker.dto.module2.AiGeneratedItemDTO;
import com.pfetracker.dto.module2.AiGenerationRequest;
import com.pfetracker.dto.module2.AiGenerationResponse;
import com.pfetracker.dto.module2.AiGenerationType;
import com.pfetracker.dto.module2.CreateTaskRequest;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.entity.module2.enums.Priority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Assistant IA génératif du Module 2.
 *
 * Cette version est gratuite et fonctionne sans API externe.
 * Elle utilise un moteur de règles intelligent pour générer :
 * - des tâches
 * - des user stories
 * - des spécifications techniques
 *
 * Plus tard, si tu veux utiliser OpenAI/Gemini/Mistral,
 * tu pourras remplacer uniquement la méthode generate(...).
 */
@Service
@RequiredArgsConstructor
public class AiGenerationService {

    private final TaskService taskService;

    /**
     * Génère des suggestions selon le type choisi dans le frontend.
     */
    public AiGenerationResponse generate(AiGenerationRequest request) {
        String normalizedPrompt = normalize(request.getPrompt());

        List<AiGeneratedItemDTO> items;

        if (request.getType() == AiGenerationType.USER_STORIES) {
            items = generateUserStories(normalizedPrompt);
        } else if (request.getType() == AiGenerationType.SPECIFICATIONS) {
            items = generateSpecifications(normalizedPrompt);
        } else {
            items = generateTasks(normalizedPrompt);
        }

        String message = "Génération IA terminée : " + items.size() + " élément(s) généré(s).";

        return new AiGenerationResponse(message, items);
    }

    /**
     * Ajoute une suggestion IA au projet comme tâche réelle.
     */
    public Task addGeneratedItemAsTask(AiAddGeneratedItemRequest request) {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest();

        createTaskRequest.setTitle(request.getTitle());
        createTaskRequest.setDescription(buildTaskDescription(request));
        createTaskRequest.setPriority(request.getPriority() != null ? request.getPriority() : Priority.NORMAL);
        createTaskRequest.setEstimatedHours(request.getEstimatedHours());
        createTaskRequest.setDeadline(LocalDate.now().plusDays(7));

        return taskService.createTask(request.getMilestoneId(), createTaskRequest);
    }

    /**
     * Ajoute toutes les suggestions IA au projet comme tâches réelles.
     */
    public List<Task> addAllGeneratedItemsAsTasks(AiAddMultipleItemsRequest request) {
        List<Task> createdTasks = new ArrayList<>();

        for (AiAddGeneratedItemRequest item : request.getItems()) {
            createdTasks.add(addGeneratedItemAsTask(item));
        }

        return createdTasks;
    }

    private List<AiGeneratedItemDTO> generateTasks(String prompt) {
        List<AiGeneratedItemDTO> items = new ArrayList<>();

        if (containsAny(prompt, "auth", "jwt", "login", "connexion", "utilisateur", "user")) {
            items.add(item(
                    "Concevoir le modèle utilisateur et les rôles",
                    "Définir les entités User, Role et les relations nécessaires pour gérer les permissions.",
                    Priority.HIGH,
                    4,
                    "TASK"
            ));
            items.add(item(
                    "Développer l'API d'authentification",
                    "Créer les endpoints login, register, refresh token et récupération du profil connecté.",
                    Priority.CRITICAL,
                    8,
                    "TASK"
            ));
            items.add(item(
                    "Implémenter la sécurité JWT",
                    "Configurer Spring Security, générer les tokens JWT et protéger les routes sensibles.",
                    Priority.CRITICAL,
                    8,
                    "TASK"
            ));
            items.add(item(
                    "Créer les écrans Login et Register",
                    "Développer les formulaires React pour la connexion et l'inscription avec validation.",
                    Priority.HIGH,
                    6,
                    "TASK"
            ));
            items.add(item(
                    "Tester le flux d'authentification",
                    "Tester l'inscription, la connexion, l'expiration du token et l'accès aux routes protégées.",
                    Priority.NORMAL,
                    4,
                    "TASK"
            ));

            return items;
        }

        if (containsAny(prompt, "spring", "backend", "api", "rest")) {
            items.add(item(
                    "Analyser les besoins fonctionnels du backend",
                    "Identifier les ressources principales, les rôles utilisateurs et les règles métier.",
                    Priority.HIGH,
                    4,
                    "TASK"
            ));
            items.add(item(
                    "Concevoir le schéma de base de données",
                    "Créer les tables principales, les relations et les contraintes nécessaires.",
                    Priority.HIGH,
                    5,
                    "TASK"
            ));
            items.add(item(
                    "Développer les entités JPA",
                    "Créer les classes Entity, enums et relations JPA avec Hibernate.",
                    Priority.HIGH,
                    6,
                    "TASK"
            ));
            items.add(item(
                    "Créer les repositories Spring Data JPA",
                    "Ajouter les interfaces Repository nécessaires pour accéder aux données.",
                    Priority.NORMAL,
                    3,
                    "TASK"
            ));
            items.add(item(
                    "Développer les services métier",
                    "Implémenter la logique principale dans les services Spring Boot.",
                    Priority.HIGH,
                    8,
                    "TASK"
            ));
            items.add(item(
                    "Exposer les endpoints REST",
                    "Créer les controllers REST et tester les routes avec Postman ou Swagger.",
                    Priority.HIGH,
                    6,
                    "TASK"
            ));

            return items;
        }

        if (containsAny(prompt, "react", "frontend", "interface", "dashboard", "page")) {
            items.add(item(
                    "Préparer la structure des composants React",
                    "Créer les dossiers pages, components, api, types et utils.",
                    Priority.NORMAL,
                    3,
                    "TASK"
            ));
            items.add(item(
                    "Développer les interfaces principales",
                    "Créer les pages React nécessaires selon les maquettes.",
                    Priority.HIGH,
                    7,
                    "TASK"
            ));
            items.add(item(
                    "Connecter React avec le backend",
                    "Créer les services Axios et connecter les pages aux APIs REST.",
                    Priority.HIGH,
                    5,
                    "TASK"
            ));
            items.add(item(
                    "Gérer les états de chargement et erreurs",
                    "Ajouter loading, empty states, messages d'erreur et feedback utilisateur.",
                    Priority.NORMAL,
                    4,
                    "TASK"
            ));
            items.add(item(
                    "Tester l'expérience utilisateur",
                    "Vérifier la navigation, les formulaires et l'affichage responsive.",
                    Priority.NORMAL,
                    4,
                    "TASK"
            ));

            return items;
        }

        items.add(item(
                "Analyser le besoin du projet",
                "Définir les objectifs, les acteurs, les fonctionnalités principales et les contraintes.",
                Priority.HIGH,
                4,
                "TASK"
        ));
        items.add(item(
                "Rédiger les spécifications fonctionnelles",
                "Décrire les cas d'utilisation, les règles métier et les scénarios principaux.",
                Priority.HIGH,
                5,
                "TASK"
        ));
        items.add(item(
                "Concevoir l'architecture technique",
                "Définir les modules, les composants, les APIs et la structure de la base de données.",
                Priority.HIGH,
                6,
                "TASK"
        ));
        items.add(item(
                "Développer les fonctionnalités principales",
                "Implémenter les fonctionnalités prioritaires du projet.",
                Priority.CRITICAL,
                10,
                "TASK"
        ));
        items.add(item(
                "Tester et corriger les anomalies",
                "Réaliser les tests fonctionnels et corriger les bugs détectés.",
                Priority.NORMAL,
                5,
                "TASK"
        ));
        items.add(item(
                "Préparer la documentation",
                "Rédiger la documentation technique et utilisateur.",
                Priority.NORMAL,
                4,
                "TASK"
        ));

        return items;
    }

    private List<AiGeneratedItemDTO> generateUserStories(String prompt) {
        List<AiGeneratedItemDTO> items = new ArrayList<>();

        if (containsAny(prompt, "auth", "jwt", "login", "connexion")) {
            items.add(item(
                    "En tant qu'utilisateur, je veux créer un compte",
                    "L'utilisateur doit pouvoir saisir ses informations et créer un compte sécurisé.",
                    Priority.HIGH,
                    4,
                    "USER_STORY"
            ));
            items.add(item(
                    "En tant qu'utilisateur, je veux me connecter",
                    "L'utilisateur doit pouvoir accéder à son espace avec email et mot de passe.",
                    Priority.CRITICAL,
                    4,
                    "USER_STORY"
            ));
            items.add(item(
                    "En tant qu'utilisateur connecté, je veux être authentifié par JWT",
                    "Le système doit générer un token JWT pour sécuriser les appels API.",
                    Priority.CRITICAL,
                    5,
                    "USER_STORY"
            ));
            items.add(item(
                    "En tant qu'administrateur, je veux gérer les rôles",
                    "L'administrateur doit pouvoir attribuer des rôles aux utilisateurs.",
                    Priority.HIGH,
                    4,
                    "USER_STORY"
            ));

            return items;
        }

        if (containsAny(prompt, "pfe", "jalon", "tache", "suivi", "progression")) {
            items.add(item(
                    "En tant qu'étudiant, je veux consulter l'avancement de mon PFE",
                    "L'étudiant doit voir la progression globale, les jalons et les tâches associées.",
                    Priority.HIGH,
                    5,
                    "USER_STORY"
            ));
            items.add(item(
                    "En tant qu'étudiant, je veux soumettre mes livrables",
                    "L'étudiant doit pouvoir uploader des fichiers liés à ses tâches ou jalons.",
                    Priority.HIGH,
                    4,
                    "USER_STORY"
            ));
            items.add(item(
                    "En tant qu'encadrant, je veux valider les tâches",
                    "L'encadrant doit pouvoir accepter, refuser ou demander une correction.",
                    Priority.CRITICAL,
                    5,
                    "USER_STORY"
            ));
            items.add(item(
                    "En tant qu'encadrant, je veux suivre les retards",
                    "L'encadrant doit recevoir des alertes lorsqu'un PFE est en retard.",
                    Priority.HIGH,
                    4,
                    "USER_STORY"
            ));

            return items;
        }

        items.add(item(
                "En tant qu'utilisateur, je veux accéder à un tableau de bord",
                "Le tableau de bord doit afficher les informations importantes du projet.",
                Priority.HIGH,
                4,
                "USER_STORY"
        ));
        items.add(item(
                "En tant qu'utilisateur, je veux gérer mes données",
                "L'utilisateur doit pouvoir créer, modifier, consulter et supprimer ses éléments.",
                Priority.HIGH,
                6,
                "USER_STORY"
        ));
        items.add(item(
                "En tant qu'utilisateur, je veux recevoir des notifications",
                "Le système doit informer l'utilisateur des actions importantes.",
                Priority.NORMAL,
                4,
                "USER_STORY"
        ));
        items.add(item(
                "En tant qu'administrateur, je veux superviser l'activité",
                "L'administrateur doit pouvoir consulter les statistiques et suivre l'état global.",
                Priority.NORMAL,
                5,
                "USER_STORY"
        ));

        return items;
    }

    private List<AiGeneratedItemDTO> generateSpecifications(String prompt) {
        List<AiGeneratedItemDTO> items = new ArrayList<>();

        if (containsAny(prompt, "spring", "backend", "api", "rest")) {
            items.add(item(
                    "Architecture backend Spring Boot",
                    "Le backend doit être organisé en couches Controller, Service, Repository, Entity et DTO.",
                    Priority.HIGH,
                    4,
                    "SPECIFICATION"
            ));
            items.add(item(
                    "Exposition des APIs REST",
                    "Les endpoints doivent respecter les méthodes HTTP GET, POST, PUT, PATCH et DELETE selon l'action.",
                    Priority.HIGH,
                    5,
                    "SPECIFICATION"
            ));
            items.add(item(
                    "Persistance avec Spring Data JPA",
                    "Les données doivent être persistées dans MySQL via Hibernate et des repositories JPA.",
                    Priority.HIGH,
                    4,
                    "SPECIFICATION"
            ));
            items.add(item(
                    "Validation des données d'entrée",
                    "Les DTOs doivent utiliser jakarta.validation pour vérifier les champs obligatoires.",
                    Priority.NORMAL,
                    3,
                    "SPECIFICATION"
            ));

            return items;
        }

        if (containsAny(prompt, "react", "frontend", "interface")) {
            items.add(item(
                    "Architecture frontend React",
                    "Le frontend doit être structuré en pages, composants réutilisables, services API et types.",
                    Priority.HIGH,
                    4,
                    "SPECIFICATION"
            ));
            items.add(item(
                    "Communication avec le backend",
                    "Le frontend doit utiliser Axios pour consommer les APIs REST exposées par Spring Boot.",
                    Priority.HIGH,
                    4,
                    "SPECIFICATION"
            ));
            items.add(item(
                    "Gestion des rôles côté interface",
                    "L'interface doit afficher les fonctionnalités selon le rôle utilisateur.",
                    Priority.NORMAL,
                    4,
                    "SPECIFICATION"
            ));
            items.add(item(
                    "Gestion des états utilisateur",
                    "Les pages doivent gérer les états loading, empty, error et success.",
                    Priority.NORMAL,
                    3,
                    "SPECIFICATION"
            ));

            return items;
        }

        items.add(item(
                "Spécification fonctionnelle",
                "Le système doit permettre aux utilisateurs de créer, consulter, modifier et suivre les éléments du projet.",
                Priority.HIGH,
                5,
                "SPECIFICATION"
        ));
        items.add(item(
                "Spécification technique",
                "L'application doit utiliser React côté frontend, Spring Boot côté backend et MySQL comme base de données.",
                Priority.HIGH,
                5,
                "SPECIFICATION"
        ));
        items.add(item(
                "Spécification sécurité",
                "Les APIs sensibles doivent être protégées par une authentification JWT et un contrôle des rôles.",
                Priority.CRITICAL,
                5,
                "SPECIFICATION"
        ));
        items.add(item(
                "Spécification qualité",
                "Le système doit être testé, documenté et facilement maintenable.",
                Priority.NORMAL,
                4,
                "SPECIFICATION"
        ));

        return items;
    }

    private AiGeneratedItemDTO item(
            String title,
            String description,
            Priority priority,
            Integer estimatedHours,
            String itemType
    ) {
        return new AiGeneratedItemDTO(title, description, priority, estimatedHours, itemType);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.toLowerCase().trim();
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private String buildTaskDescription(AiAddGeneratedItemRequest request) {
        String type = request.getItemType() != null ? request.getItemType() : "AI_GENERATED";

        return "[Généré par Assistant IA - " + type + "]\n\n"
                + (request.getDescription() != null ? request.getDescription() : "");
    }
}