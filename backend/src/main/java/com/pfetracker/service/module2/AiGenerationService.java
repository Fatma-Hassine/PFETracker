package com.pfetracker.service.module2;

import com.pfetracker.dto.module2.AiAddGeneratedItemRequest;
import com.pfetracker.dto.module2.AiAddMultipleItemsRequest;
import com.pfetracker.dto.module2.AiGeneratedItemDTO;
import com.pfetracker.dto.module2.AiGenerationRequest;
import com.pfetracker.dto.module2.AiGenerationResponse;
import com.pfetracker.dto.module2.AiGenerationType;
import com.pfetracker.dto.module2.CreateTaskRequest;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.Task;
import com.pfetracker.entity.module2.enums.Priority;
import com.pfetracker.repository.module2.PfeRepository;
import com.pfetracker.service.module2.ai.AiJsonParserService;
import com.pfetracker.service.module2.ai.AiPromptBuilderService;
import com.pfetracker.service.module2.ai.GeminiClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service principal de l'assistant IA.
 *
 * Fonctionnement :
 * 1. Si Gemini est activé et configuré :
 *    - on construit un prompt complet
 *    - on appelle Gemini
 *    - on parse le JSON retourné
 *
 * 2. Si Gemini échoue :
 *    - on utilise le moteur de règles local comme fallback
 */
@Service
@RequiredArgsConstructor
public class AiGenerationService {

    private final TaskService taskService;
    private final PfeRepository pfeRepository;

    private final GeminiClientService geminiClientService;
    private final AiPromptBuilderService aiPromptBuilderService;
    private final AiJsonParserService aiJsonParserService;

    public AiGenerationResponse generate(AiGenerationRequest request) {
        List<AiGeneratedItemDTO> geminiItems = tryGenerateWithGemini(request);

        if (!geminiItems.isEmpty()) {
            return new AiGenerationResponse(
                    "Génération IA Gemini terminée : " + geminiItems.size() + " élément(s) généré(s).",
                    geminiItems
            );
        }

        List<AiGeneratedItemDTO> fallbackItems = generateWithRules(request);

        return new AiGenerationResponse(
                "Génération locale terminée : " + fallbackItems.size() + " élément(s) généré(s).",
                fallbackItems
        );
    }

    private List<AiGeneratedItemDTO> tryGenerateWithGemini(AiGenerationRequest request) {
        try {
            if (!geminiClientService.canUseGemini()) {
                return new ArrayList<>();
            }

            Pfe pfe = null;

            if (request.getPfeId() != null) {
                pfe = pfeRepository.findById(request.getPfeId()).orElse(null);
            }

            String prompt = aiPromptBuilderService.buildPrompt(request, pfe);
            String rawGeminiJson = geminiClientService.generateJson(prompt);

            List<AiGeneratedItemDTO> items = aiJsonParserService.parseItems(rawGeminiJson);

            if (items == null || items.isEmpty()) {
                return new ArrayList<>();
            }

            return items;
        } catch (Exception exception) {
            System.err.println("Fallback IA locale après erreur Gemini : " + exception.getMessage());
            return new ArrayList<>();
        }
    }

    private List<AiGeneratedItemDTO> generateWithRules(AiGenerationRequest request) {
        String normalizedPrompt = normalize(request.getPrompt());

        if (request.getType() == AiGenerationType.MILESTONES) {
            return generateMilestones(normalizedPrompt);
        }

        if (request.getType() == AiGenerationType.USER_STORIES) {
            return generateUserStories(normalizedPrompt);
        }

        if (request.getType() == AiGenerationType.SPECIFICATIONS) {
            return generateSpecifications(normalizedPrompt);
        }

        return generateTasks(normalizedPrompt);
    }

    public Task addGeneratedItemAsTask(AiAddGeneratedItemRequest request) {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest();

        createTaskRequest.setTitle(request.getTitle());
        createTaskRequest.setDescription(buildTaskDescription(request));
        createTaskRequest.setPriority(request.getPriority() != null ? request.getPriority() : Priority.NORMAL);
        createTaskRequest.setEstimatedHours(request.getEstimatedHours());
        createTaskRequest.setDeadline(LocalDate.now().plusDays(7));

        return taskService.createTask(request.getMilestoneId(), createTaskRequest);
    }

    public List<Task> addAllGeneratedItemsAsTasks(AiAddMultipleItemsRequest request) {
        List<Task> createdTasks = new ArrayList<>();

        for (AiAddGeneratedItemRequest item : request.getItems()) {
            createdTasks.add(addGeneratedItemAsTask(item));
        }

        return createdTasks;
    }

    private List<AiGeneratedItemDTO> generateMilestones(String prompt) {
        List<AiGeneratedItemDTO> items = new ArrayList<>();
        LocalDate start = LocalDate.now();

        if (containsAny(prompt, "web", "react", "spring", "api", "mysql", "frontend", "backend")) {
            items.add(milestoneItem(
                    "Cadrage et analyse du besoin",
                    "Définir le périmètre du projet, les acteurs, les besoins fonctionnels et les contraintes.",
                    "Fiche de cadrage / cahier d'analyse",
                    start,
                    start.plusWeeks(1),
                    15.0
            ));

            items.add(milestoneItem(
                    "Spécification fonctionnelle",
                    "Rédiger les cas d'utilisation, les règles métier et les scénarios principaux.",
                    "Document de spécifications fonctionnelles",
                    start.plusWeeks(1),
                    start.plusWeeks(3),
                    15.0
            ));

            items.add(milestoneItem(
                    "Conception technique et architecture",
                    "Préparer l'architecture globale, la base de données, les diagrammes UML et la structure des APIs.",
                    "Dossier de conception technique",
                    start.plusWeeks(3),
                    start.plusWeeks(5),
                    20.0
            ));

            items.add(milestoneItem(
                    "Développement backend",
                    "Développer les entités, services, repositories, controllers REST et la logique métier.",
                    "Backend Spring Boot fonctionnel",
                    start.plusWeeks(5),
                    start.plusWeeks(9),
                    20.0
            ));

            items.add(milestoneItem(
                    "Développement frontend",
                    "Développer les interfaces React, connecter les APIs et gérer les états utilisateur.",
                    "Frontend React connecté au backend",
                    start.plusWeeks(9),
                    start.plusWeeks(12),
                    15.0
            ));

            items.add(milestoneItem(
                    "Tests, validation et documentation",
                    "Tester les fonctionnalités, corriger les anomalies, préparer la documentation et la soutenance.",
                    "Rapport de tests / documentation / support de soutenance",
                    start.plusWeeks(12),
                    start.plusWeeks(16),
                    15.0
            ));

            return items;
        }

        items.add(milestoneItem(
                "Analyse du sujet",
                "Comprendre le sujet, identifier les objectifs et les contraintes du projet.",
                "Document d'analyse",
                start,
                start.plusWeeks(2),
                20.0
        ));

        items.add(milestoneItem(
                "Conception",
                "Définir l'architecture, les modèles et les choix techniques.",
                "Dossier de conception",
                start.plusWeeks(2),
                start.plusWeeks(5),
                25.0
        ));

        items.add(milestoneItem(
                "Réalisation",
                "Développer les fonctionnalités principales du projet.",
                "Version fonctionnelle",
                start.plusWeeks(5),
                start.plusWeeks(11),
                30.0
        ));

        items.add(milestoneItem(
                "Tests et validation",
                "Tester, corriger et valider le comportement du système.",
                "Rapport de tests",
                start.plusWeeks(11),
                start.plusWeeks(14),
                15.0
        ));

        items.add(milestoneItem(
                "Rapport et soutenance",
                "Finaliser le rapport et préparer la présentation.",
                "Rapport final et slides",
                start.plusWeeks(14),
                start.plusWeeks(16),
                10.0
        ));

        return items;
    }

    private List<AiGeneratedItemDTO> generateTasks(String prompt) {
        List<AiGeneratedItemDTO> items = new ArrayList<>();

        if (containsAny(prompt, "auth", "jwt", "login", "connexion", "utilisateur", "user")) {
            items.add(taskItem(
                    "Concevoir le modèle utilisateur et les rôles",
                    "Définir les entités User, Role et les relations nécessaires pour gérer les permissions.",
                    Priority.HIGH,
                    4
            ));

            items.add(taskItem(
                    "Développer l'API d'authentification",
                    "Créer les endpoints login, register, refresh token et récupération du profil connecté.",
                    Priority.CRITICAL,
                    8
            ));

            items.add(taskItem(
                    "Implémenter la sécurité JWT",
                    "Configurer Spring Security, générer les tokens JWT et protéger les routes sensibles.",
                    Priority.CRITICAL,
                    8
            ));

            items.add(taskItem(
                    "Créer les écrans Login et Register",
                    "Développer les formulaires React pour la connexion et l'inscription avec validation.",
                    Priority.HIGH,
                    6
            ));

            return items;
        }

        if (containsAny(prompt, "spring", "backend", "api", "rest")) {
            items.add(taskItem(
                    "Analyser les besoins fonctionnels du backend",
                    "Identifier les ressources principales, les rôles utilisateurs et les règles métier.",
                    Priority.HIGH,
                    4
            ));

            items.add(taskItem(
                    "Concevoir le schéma de base de données",
                    "Créer les tables principales, les relations et les contraintes nécessaires.",
                    Priority.HIGH,
                    5
            ));

            items.add(taskItem(
                    "Développer les entités JPA",
                    "Créer les classes Entity, enums et relations JPA avec Hibernate.",
                    Priority.HIGH,
                    6
            ));

            items.add(taskItem(
                    "Créer les repositories Spring Data JPA",
                    "Ajouter les interfaces Repository nécessaires pour accéder aux données.",
                    Priority.NORMAL,
                    3
            ));

            items.add(taskItem(
                    "Développer les services métier",
                    "Implémenter la logique principale dans les services Spring Boot.",
                    Priority.HIGH,
                    8
            ));

            items.add(taskItem(
                    "Exposer les endpoints REST",
                    "Créer les controllers REST et tester les routes avec Postman.",
                    Priority.HIGH,
                    6
            ));

            return items;
        }

        if (containsAny(prompt, "react", "frontend", "interface", "dashboard", "page")) {
            items.add(taskItem(
                    "Préparer la structure des composants React",
                    "Créer les dossiers pages, components, api, types et utils.",
                    Priority.NORMAL,
                    3
            ));

            items.add(taskItem(
                    "Développer les interfaces principales",
                    "Créer les pages React nécessaires selon les maquettes.",
                    Priority.HIGH,
                    7
            ));

            items.add(taskItem(
                    "Connecter React avec le backend",
                    "Créer les services Axios et connecter les pages aux APIs REST.",
                    Priority.HIGH,
                    5
            ));

            items.add(taskItem(
                    "Gérer les états de chargement et erreurs",
                    "Ajouter loading, empty states, messages d'erreur et feedback utilisateur.",
                    Priority.NORMAL,
                    4
            ));

            return items;
        }

        items.add(taskItem(
                "Analyser le besoin du projet",
                "Définir les objectifs, les acteurs, les fonctionnalités principales et les contraintes.",
                Priority.HIGH,
                4
        ));

        items.add(taskItem(
                "Rédiger les spécifications fonctionnelles",
                "Décrire les cas d'utilisation, les règles métier et les scénarios principaux.",
                Priority.HIGH,
                5
        ));

        items.add(taskItem(
                "Concevoir l'architecture technique",
                "Définir les modules, les composants, les APIs et la structure de la base de données.",
                Priority.HIGH,
                6
        ));

        items.add(taskItem(
                "Développer les fonctionnalités principales",
                "Implémenter les fonctionnalités prioritaires du projet.",
                Priority.CRITICAL,
                10
        ));

        items.add(taskItem(
                "Tester et corriger les anomalies",
                "Réaliser les tests fonctionnels et corriger les bugs détectés.",
                Priority.NORMAL,
                5
        ));

        return items;
    }

    private List<AiGeneratedItemDTO> generateUserStories(String prompt) {
        List<AiGeneratedItemDTO> items = new ArrayList<>();

        if (containsAny(prompt, "auth", "jwt", "login", "connexion")) {
            items.add(userStoryItem(
                    "En tant qu'utilisateur, je veux créer un compte",
                    "L'utilisateur doit pouvoir saisir ses informations et créer un compte sécurisé.",
                    Priority.HIGH,
                    4
            ));

            items.add(userStoryItem(
                    "En tant qu'utilisateur, je veux me connecter",
                    "L'utilisateur doit pouvoir accéder à son espace avec email et mot de passe.",
                    Priority.CRITICAL,
                    4
            ));

            items.add(userStoryItem(
                    "En tant qu'utilisateur connecté, je veux être authentifié par JWT",
                    "Le système doit générer un token JWT pour sécuriser les appels API.",
                    Priority.CRITICAL,
                    5
            ));

            return items;
        }

        items.add(userStoryItem(
                "En tant qu'étudiant, je veux consulter l'avancement de mon PFE",
                "L'étudiant doit voir la progression globale, les jalons et les tâches associées.",
                Priority.HIGH,
                5
        ));

        items.add(userStoryItem(
                "En tant qu'étudiant, je veux soumettre mes livrables",
                "L'étudiant doit pouvoir uploader des fichiers liés à ses tâches ou jalons.",
                Priority.HIGH,
                4
        ));

        items.add(userStoryItem(
                "En tant qu'encadrant, je veux valider les tâches",
                "L'encadrant doit pouvoir accepter, refuser ou demander une correction.",
                Priority.CRITICAL,
                5
        ));

        items.add(userStoryItem(
                "En tant qu'encadrant, je veux suivre les retards",
                "L'encadrant doit recevoir des alertes lorsqu'un PFE est en retard.",
                Priority.HIGH,
                4
        ));

        return items;
    }

    private List<AiGeneratedItemDTO> generateSpecifications(String prompt) {
        List<AiGeneratedItemDTO> items = new ArrayList<>();

        if (containsAny(prompt, "spring", "backend", "api", "rest")) {
            items.add(specificationItem(
                    "Architecture backend Spring Boot",
                    "Le backend doit être organisé en couches Controller, Service, Repository, Entity et DTO.",
                    Priority.HIGH,
                    4
            ));

            items.add(specificationItem(
                    "Exposition des APIs REST",
                    "Les endpoints doivent respecter les méthodes HTTP GET, POST, PUT, PATCH et DELETE selon l'action.",
                    Priority.HIGH,
                    5
            ));

            items.add(specificationItem(
                    "Persistance avec Spring Data JPA",
                    "Les données doivent être persistées dans MySQL via Hibernate et des repositories JPA.",
                    Priority.HIGH,
                    4
            ));

            return items;
        }

        items.add(specificationItem(
                "Architecture frontend React",
                "Le frontend doit être structuré en pages, composants réutilisables, services API et types.",
                Priority.HIGH,
                4
        ));

        items.add(specificationItem(
                "Architecture backend Spring Boot",
                "Le backend doit être organisé en couches Controller, Service, Repository, Entity et DTO.",
                Priority.HIGH,
                4
        ));

        items.add(specificationItem(
                "Communication avec le backend",
                "Le frontend doit utiliser Axios pour consommer les APIs REST exposées par Spring Boot.",
                Priority.HIGH,
                4
        ));

        items.add(specificationItem(
                "Sécurité JWT",
                "Les APIs sensibles doivent être protégées par une authentification JWT et un contrôle des rôles.",
                Priority.CRITICAL,
                5
        ));

        return items;
    }

    private AiGeneratedItemDTO taskItem(
            String title,
            String description,
            Priority priority,
            Integer estimatedHours
    ) {
        AiGeneratedItemDTO item = new AiGeneratedItemDTO();

        item.setTitle(title);
        item.setDescription(description);
        item.setPriority(priority);
        item.setEstimatedHours(estimatedHours);
        item.setItemType("TASK");
        item.setExpectedDeliverable(null);
        item.setPlannedStartDate(null);
        item.setPlannedEndDate(null);
        item.setWeight(null);

        return item;
    }

    private AiGeneratedItemDTO userStoryItem(
            String title,
            String description,
            Priority priority,
            Integer estimatedHours
    ) {
        AiGeneratedItemDTO item = taskItem(title, description, priority, estimatedHours);
        item.setItemType("USER_STORY");
        return item;
    }

    private AiGeneratedItemDTO specificationItem(
            String title,
            String description,
            Priority priority,
            Integer estimatedHours
    ) {
        AiGeneratedItemDTO item = taskItem(title, description, priority, estimatedHours);
        item.setItemType("SPECIFICATION");
        return item;
    }

    private AiGeneratedItemDTO milestoneItem(
            String title,
            String description,
            String expectedDeliverable,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate,
            Double weight
    ) {
        AiGeneratedItemDTO item = new AiGeneratedItemDTO();

        item.setTitle(title);
        item.setDescription(description);
        item.setPriority(Priority.NORMAL);
        item.setEstimatedHours(null);
        item.setItemType("MILESTONE");
        item.setExpectedDeliverable(expectedDeliverable);
        item.setPlannedStartDate(plannedStartDate);
        item.setPlannedEndDate(plannedEndDate);
        item.setWeight(weight);

        return item;
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