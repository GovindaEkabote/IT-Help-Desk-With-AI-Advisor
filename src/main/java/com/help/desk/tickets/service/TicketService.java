package com.help.desk.tickets.service;

import com.help.desk.tickets.dto.request.TicketRequest;
import com.help.desk.tickets.dto.response.TicketResponse;
import com.help.desk.tickets.enums.Status;
import com.help.desk.tickets.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
//    Page<TicketResponse> getTicketsByAssignedToId(Long userId, Pageable pageable);
    Page<TicketResponse> getResolvedTicketsByUser(Long userId, Pageable pageable);
    Page<TicketResponse> getPendingTicketsByUser(Long userId, Pageable pageable);



}
