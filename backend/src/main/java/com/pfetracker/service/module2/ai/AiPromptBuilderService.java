package com.pfetracker.service.module2.ai;

import com.pfetracker.dto.module2.AiGenerationRequest;
import com.pfetracker.dto.module2.AiGenerationType;
import com.pfetracker.entity.module2.Pfe;
import org.springframework.stereotype.Service;

/**
 * Construit un prompt clair pour Gemini.
 *
 * Objectif :
 * - donner le contexte du PFE
 * - préciser le type de génération demandé
 * - forcer Gemini à répondre en JSON propre
 */
@Service
public class AiPromptBuilderService {

    public String buildPrompt(AiGenerationRequest request, Pfe pfe) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Tu es un assistant IA spécialisé dans la gestion et le suivi des projets de fin d'études PFE.\n");
        prompt.append("Tu dois aider à planifier un projet PFE en générant des éléments structurés.\n\n");

        prompt.append("CONTEXTE DU PFE :\n");

        if (pfe != null) {
            prompt.append("- Titre : ").append(nullToEmpty(pfe.getTitle())).append("\n");
            prompt.append("- Description : ").append(nullToEmpty(pfe.getDescription())).append("\n");
            prompt.append("- Problématique : ").append(nullToEmpty(pfe.getProblemStatement())).append("\n");
            prompt.append("- Objectifs : ").append(nullToEmpty(pfe.getObjectives())).append("\n");
            prompt.append("- Technologies : ").append(nullToEmpty(pfe.getTechnologies())).append("\n");
            prompt.append("- Département : ").append(nullToEmpty(pfe.getDepartment())).append("\n");
            prompt.append("- Étudiant : ").append(nullToEmpty(pfe.getStudentName())).append("\n");
            prompt.append("- Encadrant : ").append(nullToEmpty(pfe.getSupervisorName())).append("\n");
            prompt.append("- Date début : ").append(pfe.getStartDate() != null ? pfe.getStartDate() : "").append("\n");
            prompt.append("- Date soutenance : ").append(pfe.getDefenseDate() != null ? pfe.getDefenseDate() : "").append("\n");
        } else {
            prompt.append("- Aucun PFE détaillé trouvé.\n");
        }

        prompt.append("\nDEMANDE UTILISATEUR :\n");
        prompt.append(request.getPrompt()).append("\n\n");

        prompt.append("TYPE DE GENERATION DEMANDE : ").append(request.getType()).append("\n\n");

        if (request.getType() == AiGenerationType.MILESTONES) {
            appendMilestonesInstructions(prompt);
        } else if (request.getType() == AiGenerationType.TASKS) {
            appendTasksInstructions(prompt);
        } else if (request.getType() == AiGenerationType.USER_STORIES) {
            appendUserStoriesInstructions(prompt);
        } else if (request.getType() == AiGenerationType.SPECIFICATIONS) {
            appendSpecificationsInstructions(prompt);
        }

        appendJsonRules(prompt);

        return prompt.toString();
    }

    private void appendMilestonesInstructions(StringBuilder prompt) {
        prompt.append("GENERE DES JALONS PFE.\n");
        prompt.append("Chaque jalon doit représenter une phase importante du projet.\n");
        prompt.append("Propose entre 5 et 7 jalons maximum.\n");
        prompt.append("Les poids doivent totaliser environ 100.\n");
        prompt.append("Les dates doivent être cohérentes et au format yyyy-MM-dd.\n");
        prompt.append("itemType doit toujours être MILESTONE.\n");
        prompt.append("priority doit être NORMAL.\n");
        prompt.append("estimatedHours doit être null.\n\n");
    }

    private void appendTasksInstructions(StringBuilder prompt) {
        prompt.append("GENERE DES TACHES.\n");
        prompt.append("Chaque tâche doit être concrète, réalisable et liée au PFE.\n");
        prompt.append("Propose entre 5 et 8 tâches.\n");
        prompt.append("itemType doit toujours être TASK.\n");
        prompt.append("priority doit être LOW, NORMAL, HIGH ou CRITICAL.\n");
        prompt.append("estimatedHours doit être un nombre entier réaliste.\n");
        prompt.append("Les champs de jalon expectedDeliverable, plannedStartDate, plannedEndDate et weight doivent être null.\n\n");
    }

    private void appendUserStoriesInstructions(StringBuilder prompt) {
        prompt.append("GENERE DES USER STORIES.\n");
        prompt.append("Chaque user story doit commencer par une formulation du type : En tant que..., je veux..., afin de...\n");
        prompt.append("Propose entre 4 et 7 user stories.\n");
        prompt.append("itemType doit toujours être USER_STORY.\n");
        prompt.append("priority doit être LOW, NORMAL, HIGH ou CRITICAL.\n");
        prompt.append("estimatedHours peut être un entier réaliste.\n");
        prompt.append("Les champs de jalon expectedDeliverable, plannedStartDate, plannedEndDate et weight doivent être null.\n\n");
    }

    private void appendSpecificationsInstructions(StringBuilder prompt) {
        prompt.append("GENERE DES SPECIFICATIONS FONCTIONNELLES OU TECHNIQUES.\n");
        prompt.append("Chaque spécification doit être claire, exploitable et adaptée au projet.\n");
        prompt.append("Propose entre 4 et 7 spécifications.\n");
        prompt.append("itemType doit toujours être SPECIFICATION.\n");
        prompt.append("priority doit être LOW, NORMAL, HIGH ou CRITICAL.\n");
        prompt.append("estimatedHours peut être un entier ou null.\n");
        prompt.append("Les champs de jalon expectedDeliverable, plannedStartDate, plannedEndDate et weight doivent être null.\n\n");
    }

    private void appendJsonRules(StringBuilder prompt) {
        prompt.append("REGLES STRICTES DE SORTIE :\n");
        prompt.append("Retourne uniquement un JSON valide.\n");
        prompt.append("Ne retourne pas de markdown.\n");
        prompt.append("Ne retourne pas de texte avant ou après le JSON.\n");
        prompt.append("Ne mets pas ```json.\n");
        prompt.append("Le format exact doit être :\n\n");

        prompt.append("{\n");
        prompt.append("  \"items\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"title\": \"Titre court\",\n");
        prompt.append("      \"description\": \"Description claire\",\n");
        prompt.append("      \"priority\": \"NORMAL\",\n");
        prompt.append("      \"estimatedHours\": 4,\n");
        prompt.append("      \"itemType\": \"TASK\",\n");
        prompt.append("      \"expectedDeliverable\": null,\n");
        prompt.append("      \"plannedStartDate\": null,\n");
        prompt.append("      \"plannedEndDate\": null,\n");
        prompt.append("      \"weight\": null\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");

        prompt.append("Champs obligatoires pour chaque item : title, description, priority, estimatedHours, itemType, expectedDeliverable, plannedStartDate, plannedEndDate, weight.\n");
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}