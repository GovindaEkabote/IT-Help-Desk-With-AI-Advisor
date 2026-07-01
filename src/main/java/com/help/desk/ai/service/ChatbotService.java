// src/main/java/com/help/desk/ai/service/ChatbotService.java
package com.help.desk.ai.service;

import com.help.desk.ai.client.OllamaClient;
import com.help.desk.ai.dto.ChatbotRequest;
import com.help.desk.ai.dto.response.ChatbotResponse;
import com.help.desk.ai.prompt.PromptBuilder;
import com.help.desk.knowledgebase.model.KnowledgeModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final OllamaClient ollamaClient;
    private final KnowledgeBaseSearchService knowledgeBaseSearchService;

    public ChatbotResponse chat(ChatbotRequest request) {
        String userMessage = request.getMessage();

        try {
            // STEP 1: Search Knowledge Base
            List<KnowledgeModel> relevantArticles = knowledgeBaseSearchService
                    .searchKnowledgeBase(userMessage);

            boolean hasMatch = !relevantArticles.isEmpty();
            List<String> sources = relevantArticles.stream()
                    .map(KnowledgeModel::getTitle)
                    .collect(Collectors.toList());

            log.info("Chatbot query: '{}' - Found {} articles", userMessage, relevantArticles.size());

            // STEP 2: Build context from Knowledge Base
            String context = "";
            if (hasMatch) {
                context = buildContextFromArticles(relevantArticles);
                log.info("Context built from {} articles", relevantArticles.size());
            } else {
                log.info("No matching articles found for query: {}", userMessage);
            }

            // STEP 3: Build prompt with context
            String prompt;
            if (hasMatch) {
                prompt = String.format(PromptBuilder.CHATBOT_WITH_KB_PROMPT, context, userMessage);
            } else {
                prompt = String.format(PromptBuilder.CHATBOT_PROMPT, userMessage);
            }

            // STEP 4: Get AI response
            String aiResponse = ollamaClient.generateResponse(prompt);

            if (aiResponse != null && !aiResponse.isEmpty()) {
                return ChatbotResponse.builder()
                        .response(aiResponse)
                        .hasKnowledgeBaseMatch(hasMatch)
                        .sources(sources)
                        .build();
            }

        } catch (Exception e) {
            log.error("Error in chatbot: {}", e.getMessage(), e);
        }

        // Fallback responses
        return getFallbackResponse(userMessage);
    }

    /**
     * Build context from relevant articles
     */
    private String buildContextFromArticles(List<KnowledgeModel> articles) {
        StringBuilder context = new StringBuilder();
        context.append("I found these relevant articles in our knowledge base:\n\n");

        for (int i = 0; i < articles.size(); i++) {
            KnowledgeModel article = articles.get(i);
            context.append("--- Article ").append(i + 1).append(": ").append(article.getTitle()).append(" ---\n");
            context.append("Category: ").append(article.getCategory()).append("\n");
            context.append("Content: ").append(article.getContent()).append("\n");

            if (article.getTags() != null && !article.getTags().isEmpty()) {
                context.append("Tags: ").append(article.getTags()).append("\n");
            }
            context.append("\n");
        }

        context.append("Based on these articles, please provide a helpful response.");
        return context.toString();
    }

    /**
     * Enhanced fallback with Knowledge Base
     */
    private ChatbotResponse getFallbackResponse(String message) {
        // First try to find a direct match in knowledge base
        List<KnowledgeModel> articles = knowledgeBaseSearchService.searchKnowledgeBase(message);

        if (!articles.isEmpty()) {
            KnowledgeModel bestMatch = articles.get(0);
            List<String> sources = articles.stream()
                    .map(KnowledgeModel::getTitle)
                    .collect(Collectors.toList());

            String response = String.format(
                    "Based on our knowledge base, I found this information:\n\n%s\n\n" +
                            "📄 Article: %s\n" +
                            "📂 Category: %s\n\n" +
                            "💡 Is this helpful? If not, please contact IT support for personalized assistance.",
                    bestMatch.getContent(),
                    bestMatch.getTitle(),
                    bestMatch.getCategory()
            );

            return ChatbotResponse.builder()
                    .response(response)
                    .hasKnowledgeBaseMatch(true)
                    .sources(sources)
                    .build();
        }

        // If no knowledge base match, use generic fallback
        String response = getGenericFallback(message);

        return ChatbotResponse.builder()
                .response(response)
                .hasKnowledgeBaseMatch(false)
                .sources(new ArrayList<>())
                .build();
    }

    /**
     * Generic fallback responses
     */
    private String getGenericFallback(String message) {
        String lower = message.toLowerCase();
        String response;

        if (lower.contains("password") || lower.contains("login") || lower.contains("access")) {
            response = "🔐 For password/access issues:\n" +
                    "1️⃣ Go to the login page\n" +
                    "2️⃣ Click 'Forgot Password'\n" +
                    "3️⃣ Enter your email address\n" +
                    "4️⃣ Follow the instructions in the reset email\n" +
                    "5️⃣ If you don't receive the email, check your spam folder\n\n" +
                    "📞 For immediate assistance, contact IT Support.";
        } else if (lower.contains("email") || lower.contains("outlook") || lower.contains("mail")) {
            response = "📧 For email issues:\n" +
                    "1️⃣ Check your internet connection\n" +
                    "2️⃣ Try restarting Outlook\n" +
                    "3️⃣ Clear the outbox folder\n" +
                    "4️⃣ Verify your email settings\n" +
                    "5️⃣ If problem persists, contact IT support.";
        } else if (lower.contains("printer") || lower.contains("print")) {
            response = "🖨️ For printer issues:\n" +
                    "1️⃣ Check if printer is turned on\n" +
                    "2️⃣ Verify network/USB connection\n" +
                    "3️⃣ Check paper and ink levels\n" +
                    "4️⃣ Try restarting printer and computer\n" +
                    "5️⃣ Reinstall printer drivers if needed";
        } else if (lower.contains("vpn") || lower.contains("wifi") || lower.contains("network")) {
            response = "🌐 For network issues:\n" +
                    "1️⃣ Check your internet connection\n" +
                    "2️⃣ Try restarting your router\n" +
                    "3️⃣ Verify VPN credentials\n" +
                    "4️⃣ Test connection with a different device\n" +
                    "5️⃣ Contact network support if issues persist";
        } else if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey")) {
            response = "👋 Hello! I'm your IT support assistant. How can I help you today?\n\n" +
                    "You can ask me about:\n" +
                    "• 🔐 Password resets\n" +
                    "• 📧 Email issues\n" +
                    "• 🌐 Network connectivity\n" +
                    "• 🖨️ Printer problems\n" +
                    "• 💻 Software installations\n" +
                    "• Or any other IT-related queries";
        } else {
            response = "🤔 I understand you need help with: \"" + message + "\".\n\n" +
                    "I couldn't find specific information in our knowledge base for this issue.\n\n" +
                    "For personalized assistance:\n" +
                    "1️⃣ Create a support ticket\n" +
                    "2️⃣ Contact IT support directly\n" +
                    "3️⃣ Or try rephrasing your question\n\n" +
                    "💡 I'm here to help!";
        }

        return response;
    }
}