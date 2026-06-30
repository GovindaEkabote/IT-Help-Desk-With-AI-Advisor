package com.help.desk.ai.controller;

import com.help.desk.ai.dto.ChatbotRequest;
import com.help.desk.ai.dto.ClassificationResponse;
import com.help.desk.ai.dto.response.ChatbotResponse;
import com.help.desk.ai.dto.response.SummaryResponse;
import com.help.desk.ai.service.ChatbotService;
import com.help.desk.ai.service.IssueClassifierService;
import com.help.desk.ai.service.SupportAdvisorService;
import com.help.desk.ai.service.TicketSummarizerService;
import com.help.desk.exception.ApiResponse;
import com.help.desk.tickets.model.Ticket;
import com.help.desk.tickets.model.TicketComment;
import com.help.desk.tickets.service.CommentService;
import com.help.desk.tickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AIController {

    private final IssueClassifierService classifierService;
    private final ChatbotService chatbotService;
    private final TicketSummarizerService summarizerService;
    private final SupportAdvisorService advisorService;
    private final TicketService ticketService;
    private final CommentService commentService;

    /**
     * Classify ticket issue - Used when creating/updating tickets
     * POST /api/ai/classify
     */
    @PostMapping("/classify")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'SUPPORT', 'ADMIN')")
    public ResponseEntity<ClassificationResponse> classifyIssue(
            @RequestParam String title,
            @RequestParam String description) {
        try {
            ClassificationResponse response = classifierService.classifyIssue(title, description);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error classifying issue: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    /**
     * Chat with AI assistant - For user questions
     * POST /api/ai/chatbot
     */
    @PostMapping("/chatbot")
    public ResponseEntity<ChatbotResponse>  chat(
            @RequestBody ChatbotRequest  request
            ){
        try{
            ChatbotResponse response = chatbotService.chat(request);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.error("Error chatting with AI: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Summarize ticket conversation - For tickets with many comments
     * POST /api/ai/summarize/{ticketId}
     */
//    @PostMapping("/summarize/{ticketId}")
//    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
//    public ResponseEntity<SummaryResponse> summarizeTicket(
//            @PathVariable Long ticketId
//    ){
//        try{
//            Ticket ticket = ticketService.getTicketById(ticketId);
//            List<TicketComment> comments = commentService.getCommentsByTicketId(ticketId);
//
//        }catch (Exception e){
//            log.error("Error summarizing ticket: {}", e.getMessage());
//            return ResponseEntity.badRequest().build();
//        }
//    }
}
