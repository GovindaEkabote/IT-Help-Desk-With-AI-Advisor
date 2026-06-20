package com.help.desk.tickets.controller;

import com.help.desk.tickets.dto.request.TicketRequest;
import com.help.desk.tickets.dto.response.TicketResponse;
import com.help.desk.tickets.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','SUPER_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketRequest ticketRequest
    ){
        TicketResponse ticketResponse = ticketService.createTicket(ticketRequest);
        return ResponseEntity.ok(ticketResponse);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','IT_SUPPORT','ADMIN','SUPER_ADMIN')")
    @GetMapping("/get/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        TicketResponse ticketResponse = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticketResponse);
    }

    @PreAuthorize("hasAnyRole('IT_SUPPORT','ADMIN','SUPER_ADMIN')")
    @GetMapping("/get/ticket/{ticketNumber}")
    public ResponseEntity<TicketResponse> getTicketByNumber(@PathVariable String ticketNumber) {
        TicketResponse ticketResponse = ticketService.getTicketByNumber(ticketNumber);
        return ResponseEntity.ok(ticketResponse);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE')")
        @PutMapping("/update/{id}")
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable Long id, @Valid @RequestBody TicketRequest ticketRequest) {
        TicketResponse ticketResponse = ticketService.updateTicket(id, ticketRequest);
        return ResponseEntity.ok(ticketResponse);
    }

}
