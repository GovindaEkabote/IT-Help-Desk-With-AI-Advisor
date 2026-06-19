package com.help.desk.tickets.controller;

import com.help.desk.tickets.dto.request.TicketRequest;
import com.help.desk.tickets.dto.response.TicketResponse;
import com.help.desk.tickets.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketRequest ticketRequest
    ){
        TicketResponse ticketResponse = ticketService.createTicket(ticketRequest);
        return ResponseEntity.ok(ticketResponse);
    }
}
