package com.help.desk.tickets.service.impl;


import com.help.desk.exception.ResourceNotFoundException;
import com.help.desk.tickets.dto.request.TicketRequest;
import com.help.desk.tickets.dto.response.TicketResponse;
import com.help.desk.tickets.enums.Priority;
import com.help.desk.tickets.enums.Status;
import com.help.desk.tickets.model.Ticket;
import com.help.desk.tickets.repository.TicketRepository;
import com.help.desk.tickets.service.TicketService;
import com.help.desk.user.model.User;
import com.help.desk.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class TicketServiceImpl  implements TicketService {

    private TicketRepository ticketRepository;
    private UserRepository userRepository;


    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TicketResponse createTicket(TicketRequest request) {
        User user = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        User assignedTo = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned Not Found"));


        Ticket ticket = Ticket.builder()
                .ticketNumber(generateTicketNumber())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(Priority.valueOf(request.getPriority()))
                .status(Status.NEW)
                .source(request.getSource())
                .createdBy(user)
                .assignedTo(assignedTo)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        return mapToResponse(savedTicket);
    }

    @Override
    public TicketResponse getTicketById(Long id) {
        return null;
    }

    @Override
    public TicketResponse getTicketByNumber(String ticketNumber) {
        return null;
    }

    @Override
    public TicketResponse updateTicket(Long id, TicketRequest ticketRequest) {
        return null;
    }

    @Override
    public void deleteTicket(Long id) {

    }

    @Override
    public TicketResponse assignTicket(Long id, Long userId) {
        return null;
    }

    @Override
    public TicketResponse resolveTicket(Long id, Long userId) {
        return null;
    }

    @Override
    public TicketResponse updateTicketStatus(Long id, String status) {
        return null;
    }



//     Helper Methods

    private String generateTicketNumber() {
        return "TKT-" + System.currentTimeMillis();
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .category(ticket.getCategory())
                .createdByName(ticket.getCreatedBy().getFirstName())
                .assignedToName(
                        ticket.getAssignedTo() != null
                                ? ticket.getAssignedTo().getFirstName()
                                : null
                )
                .createdAt(ticket.getCreatedAt())
                .build();
    }
}
