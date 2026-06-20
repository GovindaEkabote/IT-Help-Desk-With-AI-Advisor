package com.help.desk.tickets.service.impl;


import com.help.desk.auth.service.AuthService;
import com.help.desk.exception.ResourceNotFoundException;
import com.help.desk.tickets.dto.request.TicketRequest;
import com.help.desk.tickets.dto.response.TicketResponse;
import com.help.desk.tickets.enums.Priority;
import com.help.desk.tickets.enums.Status;
import com.help.desk.tickets.model.Ticket;
import com.help.desk.tickets.repository.TicketRepository;
import com.help.desk.tickets.service.TicketService;
import com.help.desk.user.enums.UserRole;
import com.help.desk.user.model.User;
import com.help.desk.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service

public class TicketServiceImpl  implements TicketService {

    private TicketRepository ticketRepository;
    private UserRepository userRepository;
    private AuthService authService;


    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository,AuthService authService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public TicketResponse createTicket(TicketRequest request) {
        User currentUser = authService.getCurrentUser();

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
                .createdBy(currentUser)
                .assignedTo(assignedTo)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        return mapToResponse(savedTicket);
    }

    @Override
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        User currentUser = authService.getCurrentUser();

        UserRole role = currentUser.getRole();

        boolean hasAccess =
                        role == UserRole.SUPER_ADMIN ||
                        role == UserRole.ADMIN ||
                        role == UserRole.IT_SUPPORT ||
                        ticket.getCreatedBy().getId().equals(currentUser.getId());

        if (!hasAccess){
            throw new AccessDeniedException("You are not authorized to view this ticket");
        }
        return mapToResponse(ticket);
    }

    @Override
    public TicketResponse getTicketByNumber(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        User user = authService.getCurrentUser();

        UserRole role = user.getRole();

        boolean hasAccess =
                role == UserRole.SUPER_ADMIN ||
                        role == UserRole.ADMIN ||
                        role == UserRole.IT_SUPPORT;
        if(!hasAccess){
            throw new AccessDeniedException("You are not authorized to view this ticket");
        }
        return mapToResponse(ticket);
    }

    @Override
    public TicketResponse updateTicket(Long id, TicketRequest ticketRequest) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));
        User currentUser = authService.getCurrentUser();

        boolean hasAccess =
                ticket.getCreatedBy().getId().equals(currentUser.getId());

        if (!hasAccess) {
            throw new AccessDeniedException("You are not authorized to update this ticket");
        }

        ticket.setTitle(ticketRequest.getTitle());
        ticket.setTitle(ticketRequest.getDescription());
        ticket.setCategory(ticketRequest.getCategory());
        ticket.setTitle(ticketRequest.getPriority());
        ticket.setUpdatedAt(LocalDateTime.now());

        User assignedTo = null;
        if (ticketRequest.getAssignedToId() != null) {
            assignedTo = userRepository.findById(ticketRequest.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned Not Found"));
            ticket.setAssignedTo(assignedTo);
        }
        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToResponse(updatedTicket);
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
