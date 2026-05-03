package com.pfetracker.service.module2.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Service responsable de l'appel réel à Gemini.
 *
 * Il reçoit un prompt complet, l'envoie à Gemini, puis retourne le texte généré.
 * Si Gemini est désactivé ou si la clé API est absente, il renvoie null.
 */
@Service
public class GeminiClientService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Getter
    private final boolean enabled;

    private final String apiKey;
    private final String apiUrl;

    public GeminiClientService(
            RestTemplateBuilder restTemplateBuilder,
            ObjectMapper objectMapper,
            @Value("${gemini.enabled:false}") boolean enabled,
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.api.url:}") String apiUrl,
            @Value("${gemini.timeout-ms:30000}") long timeoutMs
    ) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();

        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public boolean canUseGemini() {
        return enabled
                && apiKey != null
                && !apiKey.isBlank()
                && apiUrl != null
                && !apiUrl.isBlank();
    }

    public String generateJson(String prompt) {
        if (!canUseGemini()) {
            return null;
        }

        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(apiUrl)
                    .queryParam("key", apiKey)
                    .toUriString();

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "role", "user",
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.7,
                            "topP", 0.9,
                            "maxOutputTokens", 4096,
                            "responseMimeType", "application/json"
                    )
            );

            String response = restTemplate.postForObject(url, body, String.class);

            if (response == null || response.isBlank()) {
                return null;
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                return null;
            }

            JsonNode parts = candidates.get(0)
                    .path("content")
                    .path("parts");

            if (!parts.isArray() || parts.isEmpty()) {
                return null;
            }

            return parts.get(0).path("text").asText(null);
        } catch (Exception exception) {
            System.err.println("Erreur Gemini : " + exception.getMessage());
            return null;
        }
    }
}