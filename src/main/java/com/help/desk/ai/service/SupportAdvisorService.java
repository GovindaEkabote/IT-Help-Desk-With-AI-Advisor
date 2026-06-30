package com.help.desk.ai.service;

import com.help.desk.ai.client.OllamaClient;
import com.help.desk.ai.dto.response.SuggestionResponse;
import com.help.desk.ai.prompt.PromptBuilder;
import com.help.desk.tickets.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupportAdvisorService {

    private final OllamaClient ollamaClient;

    /**
     * Get troubleshooting advice
     */
    public SuggestionResponse getAdvice(Ticket ticket) {
        try {
            String prompt = String.format(PromptBuilder.SUPPORT_ADVICE_PROMPT,
                    ticket.getDescription(),
                    ticket.getCategory(),
                    ticket.getPriority());

            String advice = ollamaClient.generateResponse(prompt);

            if (advice != null && !advice.isEmpty()) {
                return SuggestionResponse.builder()
                        .advice(advice)
                        .category(String.valueOf(ticket.getCategory()))
                        .build();
            }

        } catch (Exception e) {
            log.error("Error getting advice: {}", e.getMessage());
        }

        // Fallback advice
        return SuggestionResponse.builder()
                .advice(getFallbackAdvice(String.valueOf(ticket.getCategory())))
                .category(String.valueOf(ticket.getCategory()))
                .build();
    }

    /**
     * Fallback advice based on category
     */
    private String getFallbackAdvice(String category) {
        switch (category.toUpperCase()) {
            case "HARDWARE":
                return "1. Check all physical connections\n2. Restart the device\n3. Check power supply\n4. Test with a different device\n5. Contact hardware support if issue persists";
            case "SOFTWARE":
                return "1. Restart the application\n2. Clear cache/temporary files\n3. Check for updates\n4. Reinstall if necessary\n5. Check system requirements";
            case "NETWORK":
                return "1. Check internet connection\n2. Restart router/modem\n3. Verify VPN connection\n4. Check firewall settings\n5. Test with a different network";
            case "EMAIL":
                return "1. Check internet connection\n2. Verify email settings\n3. Clear outbox\n4. Check storage limits\n5. Try webmail access";
            default:
                return "1. Gather more information\n2. Try basic troubleshooting\n3. Check if others are affected\n4. Escalate if needed";
        }
    }
}
