package com.pfetracker.service.module2.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfetracker.dto.module2.AiGeneratedItemDTO;
import com.pfetracker.entity.module2.enums.Priority;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Transforme le JSON retourné par Gemini en liste de AiGeneratedItemDTO.
 *
 * Si Gemini retourne un JSON imparfait, ce service essaye de nettoyer le texte.
 */
@Service
public class AiJsonParserService {

    private final ObjectMapper objectMapper;

    public AiJsonParserService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<AiGeneratedItemDTO> parseItems(String rawText) {
        List<AiGeneratedItemDTO> items = new ArrayList<>();

        if (rawText == null || rawText.isBlank()) {
            return items;
        }

        try {
            String cleanedJson = cleanJson(rawText);
            JsonNode root = objectMapper.readTree(cleanedJson);

            JsonNode itemsNode;

            if (root.isArray()) {
                itemsNode = root;
            } else {
                itemsNode = root.path("items");
            }

            if (!itemsNode.isArray()) {
                return items;
            }

            for (JsonNode node : itemsNode) {
                AiGeneratedItemDTO item = new AiGeneratedItemDTO();

                item.setTitle(getText(node, "title", "Sans titre"));
                item.setDescription(getText(node, "description", ""));
                item.setPriority(parsePriority(getText(node, "priority", "NORMAL")));
                item.setEstimatedHours(getIntegerOrNull(node, "estimatedHours"));
                item.setItemType(getText(node, "itemType", "TASK"));

                item.setExpectedDeliverable(getNullableText(node, "expectedDeliverable"));
                item.setPlannedStartDate(getLocalDateOrNull(node, "plannedStartDate"));
                item.setPlannedEndDate(getLocalDateOrNull(node, "plannedEndDate"));
                item.setWeight(getDoubleOrNull(node, "weight"));

                items.add(item);
            }

            return items;
        } catch (Exception exception) {
            System.err.println("Erreur parsing JSON IA : " + exception.getMessage());
            return new ArrayList<>();
        }
    }

    private String cleanJson(String value) {
        String cleaned = value.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring("```json".length()).trim();
        }

        if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring("```".length()).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }

        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');

        if (firstBrace >= 0 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }

        return cleaned;
    }

    private String getText(JsonNode node, String field, String defaultValue) {
        JsonNode value = node.get(field);

        if (value == null || value.isNull()) {
            return defaultValue;
        }

        String text = value.asText();

        if (text == null || text.isBlank()) {
            return defaultValue;
        }

        return text;
    }

    private String getNullableText(JsonNode node, String field) {
        JsonNode value = node.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        String text = value.asText();

        if (text == null || text.isBlank() || "null".equalsIgnoreCase(text)) {
            return null;
        }

        return text;
    }

    private Integer getIntegerOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        if (value.isNumber()) {
            return value.asInt();
        }

        try {
            String text = value.asText();

            if (text == null || text.isBlank() || "null".equalsIgnoreCase(text)) {
                return null;
            }

            return Integer.parseInt(text);
        } catch (Exception exception) {
            return null;
        }
    }

    private Double getDoubleOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        if (value.isNumber()) {
            return value.asDouble();
        }

        try {
            String text = value.asText();

            if (text == null || text.isBlank() || "null".equalsIgnoreCase(text)) {
                return null;
            }

            return Double.parseDouble(text);
        } catch (Exception exception) {
            return null;
        }
    }

    private LocalDate getLocalDateOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        try {
            String text = value.asText();

            if (text == null || text.isBlank() || "null".equalsIgnoreCase(text)) {
                return null;
            }

            return LocalDate.parse(text);
        } catch (Exception exception) {
            return null;
        }
    }

    private Priority parsePriority(String value) {
        if (value == null || value.isBlank()) {
            return Priority.NORMAL;
        }

        try {
            return Priority.valueOf(value.trim().toUpperCase());
        } catch (Exception exception) {
            return Priority.NORMAL;
        }
    }
}