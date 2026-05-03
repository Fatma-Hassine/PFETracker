package com.pfetracker.service.module2;

import com.pfetracker.entity.module2.Milestone;
import com.pfetracker.entity.module2.Pfe;
import com.pfetracker.entity.module2.enums.MilestoneStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Crée automatiquement les 6 jalons standards d'un PFE.
 */
@Component
public class MilestoneFactory {

    public List<Milestone> createDefaultMilestones(Pfe pfe) {
        List<Milestone> milestones = new ArrayList<>();

        LocalDate start = pfe.getStartDate() != null ? pfe.getStartDate() : LocalDate.now();

        milestones.add(createMilestone(
                pfe,
                1,
                "Cadrage et analyse du besoin",
                "Comprendre le sujet, définir la problématique, identifier les besoins et les acteurs.",
                "Cahier d'analyse / fiche de cadrage",
                start,
                start.plusWeeks(2),
                15.0
        ));

        milestones.add(createMilestone(
                pfe,
                2,
                "Étude de l'existant et spécification",
                "Analyser les solutions existantes et préparer les spécifications fonctionnelles.",
                "Document de spécification",
                start.plusWeeks(2),
                start.plusWeeks(4),
                15.0
        ));

        milestones.add(createMilestone(
                pfe,
                3,
                "Conception",
                "Préparer l'architecture, les diagrammes UML, le modèle de données et les choix techniques.",
                "Dossier de conception",
                start.plusWeeks(4),
                start.plusWeeks(6),
                20.0
        ));

        milestones.add(createMilestone(
                pfe,
                4,
                "Développement",
                "Réaliser les fonctionnalités principales de l'application.",
                "Code source fonctionnel",
                start.plusWeeks(6),
                start.plusWeeks(12),
                25.0
        ));

        milestones.add(createMilestone(
                pfe,
                5,
                "Tests et validation",
                "Tester les fonctionnalités, corriger les bugs et valider le comportement attendu.",
                "Rapport de tests",
                start.plusWeeks(12),
                start.plusWeeks(14),
                15.0
        ));

        milestones.add(createMilestone(
                pfe,
                6,
                "Rapport final et préparation soutenance",
                "Finaliser le rapport, préparer la démonstration et les slides de soutenance.",
                "Rapport final / présentation",
                start.plusWeeks(14),
                start.plusWeeks(16),
                10.0
        ));

        return milestones;
    }

    private Milestone createMilestone(
            Pfe pfe,
            Integer orderIndex,
            String title,
            String description,
            String expectedDeliverable,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate,
            Double weight
    ) {
        Milestone milestone = new Milestone();

        milestone.setPfe(pfe);
        milestone.setOrderIndex(orderIndex);
        milestone.setTitle(title);
        milestone.setDescription(description);
        milestone.setExpectedDeliverable(expectedDeliverable);
        milestone.setPlannedStartDate(plannedStartDate);
        milestone.setPlannedEndDate(plannedEndDate);
        milestone.setWeight(weight);
        milestone.setProgress(0.0);
        milestone.setStatus(MilestoneStatus.NOT_STARTED);

        return milestone;
    }
}