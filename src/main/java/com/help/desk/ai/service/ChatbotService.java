package com.help.desk.ai.service;

import com.help.desk.ai.client.OllamaClient;
import com.help.desk.ai.dto.ChatbotRequest;
import com.help.desk.ai.dto.response.ChatbotResponse;
import com.help.desk.ai.prompt.PromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final OllamaClient ollamaClient;

    /**
     * Chat with AI assistant
     */
    public ChatbotResponse chat(ChatbotRequest request) {
        String userMessage = request.getMessage();

        try {
            // Build prompt
            String prompt = String.format(PromptBuilder.CHATBOT_PROMPT, userMessage);

            // Get AI response
            String aiResponse = ollamaClient.generateResponse(prompt);

            if (aiResponse != null && !aiResponse.isEmpty()) {
                return ChatbotResponse.builder()
                        .response(aiResponse)
                        .hasKnowledgeBaseMatch(false)
                        .sources(new ArrayList<>())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error in chatbot: {}", e.getMessage());
        }

        // Fallback responses
        return getFallbackResponse(userMessage);
    }

    /**
     * Simple fallback responses
     */
    private ChatbotResponse getFallbackResponse(String message) {
        String lower = message.toLowerCase();
        String response;

        if (lower.contains("password") || lower.contains("login") || lower.contains("access")) {
            response = "For password/access issues, please use the password reset portal or contact IT support directly.";
        } else if (lower.contains("email") || lower.contains("outlook") || lower.contains("mail")) {
            response = "For email issues:\n1. Check your internet connection\n2. Try restarting Outlook\n3. Clear the outbox folder\n4. If problem persists, contact IT support.";
        } else if (lower.contains("printer") || lower.contains("print")) {
            response = "For printer issues:\n1. Check if printer is turned on\n2. Verify network connection\n3. Check paper and ink levels\n4. Try restarting printer and computer.";
        } else if (lower.contains("vpn") || lower.contains("wifi") || lower.contains("network")) {
            response = "For network issues:\n1. Check your WiFi connection\n2. Try restarting your router\n3. Verify VPN credentials\n4. Test connection with a different device.";
        } else if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey")) {
            response = "Hello! How can I help you with your IT issue today?";
        } else {
            response = "I understand you need help with: \"" + message + "\". For detailed assistance, please create a support ticket or contact IT support directly.";
        }

        return ChatbotResponse.builder()
                .response(response)
                .hasKnowledgeBaseMatch(false)
                .sources(new ArrayList<>())
                .build();
    }

}
