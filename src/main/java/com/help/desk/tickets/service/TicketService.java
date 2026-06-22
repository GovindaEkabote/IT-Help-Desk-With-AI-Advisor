package com.help.desk.tickets.service;

import com.help.desk.tickets.dto.request.TicketRequest;
import com.help.desk.tickets.dto.response.TicketResponse;
import com.help.desk.tickets.enums.Status;
import com.help.desk.tickets.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TicketService {

    TicketResponse createTicket(TicketRequest ticketRequest);
    TicketResponse getTicketById(Long id);
    TicketResponse getTicketByNumber(String ticketNumber);
    TicketResponse updateTicket(Long id, TicketRequest ticketRequest);
    void deleteTicket(Long id);
    TicketResponse assignTicket(Long id, Long userId);
    TicketResponse claimTicket(Long id);
    TicketResponse resolveTicket(Long id, Long userId);
    TicketResponse updateTicketStatus(Long id, String status);



    // User-specific queries
    Page<TicketResponse> getTicketsByCreatedById(Pageable pageable);
    Page<TicketResponse> getResolvedTicketsByUser(Long userId, Pageable pageable);
    Page<TicketResponse> getPendingTicketsByUser(Long userId, Pageable pageable);
    Page<TicketResponse> getAllPendingTickets(Pageable pageable);


    // Ticket history for a specific user
    Page<TicketResponse> getTicketHistoryByUser(Long userId, Pageable pageable);
    Map<String , Object> getUserTicketStatistics(Long userId, Pageable pageable);

    // Admin dashboard queries
    Page<TicketResponse> getAllTickets(Pageable pageable);
    Page<TicketResponse> getTicketsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
