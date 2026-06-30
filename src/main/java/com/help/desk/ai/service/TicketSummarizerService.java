package com.help.desk.ai.service;

import com.help.desk.ai.client.OllamaClient;
import com.help.desk.ai.dto.response.SummaryResponse;
import com.help.desk.ai.prompt.PromptBuilder;
import com.help.desk.tickets.model.Ticket;
import com.help.desk.tickets.model.TicketComment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class TicketSummarizerService {

    private final OllamaClient ollamaClient;

    /**
     * Summarize ticket conversation
     */
    public SummaryResponse summarizeTicket(Ticket ticket, List<TicketComment> comments) {
        try {
            if (comments == null || comments.size() < 3) {
                return SummaryResponse.builder()
                        .summary("Ticket is still new. Current status: " + ticket.getStatus())
                        .confidence(1.0)
                        .build();
            }

            // Build conversation
            String conversation = comments.stream()
                    .map(c -> c.getCommentedBy().getFirstName() + ": " + c.getComment())
                    .limit(10) // Limit to last 10 comments
                    .collect(Collectors.joining("\n"));

            String prompt = String.format(PromptBuilder.SUMMARIZE_PROMPT,
                    ticket.getTitle(), conversation);

            String summary = ollamaClient.generateResponse(prompt);

            if (summary != null && !summary.isEmpty()) {
                return SummaryResponse.builder()
                        .summary(summary)
                        .confidence(0.8)
                        .build();
            }

        } catch (Exception e) {
            log.error("Error summarizing ticket: {}", e.getMessage());
        }

        // Fallback summary
        return SummaryResponse.builder()
                .summary("Ticket: " + ticket.getTitle() +
                        "\nStatus: " + ticket.getStatus() +
                        "\nPriority: " + ticket.getPriority() +
                        "\nHas " + (comments != null ? comments.size() : 0) + " comments.")
                .confidence(0.5)
                .build();
    }
}
