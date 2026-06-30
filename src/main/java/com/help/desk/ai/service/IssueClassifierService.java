package com.help.desk.ai.service;

import com.help.desk.ai.client.OllamaClient;
import com.help.desk.ai.dto.ClassificationResponse;
import com.help.desk.ai.prompt.PromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssueClassifierService {

    private final OllamaClient ollamaClient;
    private final ObjectMapper objectMapper;

    public ClassificationResponse classifyIssue(String title, String description) {
        String fullDescription = title + ". " + description;
        String prompt = String.format(PromptBuilder.CLASSIFY_PROMPT, fullDescription);

        try {
            String aiResponse = ollamaClient.generateResponse(prompt);

            if (aiResponse != null && !aiResponse.isEmpty()) {
                // Clean the response - extract JSON
                String jsonResponse = extractJson(aiResponse);
                Map<String, String> result = objectMapper.readValue(jsonResponse, Map.class);

                return ClassificationResponse.builder()
                        .category(result.getOrDefault("category", "OTHER"))
                        .priority(result.getOrDefault("priority", "MEDIUM"))
                        .confidence(0.8)
                        .build();
            }
        } catch (Exception e) {
            log.error("Error classifying issue: {}", e.getMessage());
        }

        // Fallback: Rule-based classification
        return fallbackClassify(fullDescription);
    }

    /**
     * Extract JSON from AI response
     */
    private String extractJson(String response) {
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}") + 1;
        if (start >= 0 && end > start) {
            return response.substring(start, end);
        }
        return "{\"category\":\"OTHER\",\"priority\":\"MEDIUM\"}";
    }

    /**
     * Fallback classification when AI fails
     */
    private ClassificationResponse fallbackClassify(String text) {
        String lower = text.toLowerCase();

        String category = "OTHER";
        if (lower.contains("printer") || lower.contains("monitor") || lower.contains("laptop") ||
                lower.contains("keyboard") || lower.contains("mouse") || lower.contains("hardware")) {
            category = "HARDWARE";
        } else if (lower.contains("excel") || lower.contains("word") || lower.contains("outlook") ||
                lower.contains("software") || lower.contains("crash") || lower.contains("install")) {
            category = "SOFTWARE";
        } else if (lower.contains("wifi") || lower.contains("vpn") || lower.contains("internet") ||
                lower.contains("network") || lower.contains("connection")) {
            category = "NETWORK";
        } else if (lower.contains("email") || lower.contains("mail") || lower.contains("send") ||
                lower.contains("receive")) {
            category = "EMAIL";
        } else if (lower.contains("access") || lower.contains("permission") || lower.contains("role") ||
                lower.contains("login")) {
            category = "ACCESS_REQUEST";
        }

        String priority = "MEDIUM";
        if (lower.contains("urgent") || lower.contains("critical") || lower.contains("emergency") ||
                lower.contains("immediate") || lower.contains("down")) {
            priority = "URGENT";
        } else if (lower.contains("high") || lower.contains("important") || lower.contains("major")) {
            priority = "HIGH";
        } else if (lower.contains("low") || lower.contains("minor") || lower.contains("small")) {
            priority = "LOW";
        }

        return ClassificationResponse.builder()
                .category(category)
                .priority(priority)
                .confidence(0.5)
                .build();
    }
}
