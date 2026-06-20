package com.help.desk.tickets.service;

import com.help.desk.tickets.dto.request.TicketRequest;
import com.help.desk.tickets.dto.response.TicketResponse;
import com.help.desk.tickets.model.Ticket;

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

}
