package com.help.desk.tickets.controller;

import com.help.desk.auth.service.AuthService;
import com.help.desk.tickets.dto.request.TicketRequest;
import com.help.desk.tickets.dto.response.TicketResponse;
import com.help.desk.tickets.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    private final TicketService ticketService;
    private final AuthService authService;

    public TicketController(TicketService ticketService, AuthService authService){
        this.ticketService = ticketService;
        this.authService =  authService;
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

    @PreAuthorize("hasAnyRole('IT_SUPPORT','ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MANAGER')")
    @PostMapping("/{ticketId}/assign/{userId}")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long ticketId,
            @PathVariable Long userId) {
        TicketResponse ticketResponse = ticketService.assignTicket(ticketId, userId);
        return ResponseEntity.ok(ticketResponse);
    }

    @PreAuthorize("hasRole('IT_SUPPORT')")
    @PostMapping("/{ticketId}/claim")
    public ResponseEntity<TicketResponse> claimTicket(@PathVariable Long ticketId) {
        TicketResponse ticketResponse = ticketService.claimTicket(ticketId);
        return ResponseEntity.ok(ticketResponse);
    }


    @PreAuthorize("hasAnyRole('IT_SUPPORT','ADMIN','SUPER_ADMIN')")
    @PostMapping("/{ticketId}/resolve/{userId}")
    public ResponseEntity<TicketResponse> resolveTicket(
            @PathVariable Long ticketId,
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                ticketService.resolveTicket(ticketId, userId));
    }

    @PreAuthorize("hasAnyRole('IT_SUPPORT','ADMIN','SUPER_ADMIN','MANAGER')")
    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long ticketId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                ticketService.updateTicketStatus(ticketId, status));
    }


    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    @GetMapping("/my-tickets")
    public ResponseEntity<Page<TicketResponse>> getTicketsByCreatedBy(
            Pageable pageable) {
        Page<TicketResponse> ticketResponses = ticketService.getTicketsByCreatedById(pageable);
        return ResponseEntity.ok(ticketResponses);
    }

    @PreAuthorize("hasRole('IT_SUPPORT')")
    @GetMapping("/my-resolved")
    public ResponseEntity<Page<TicketResponse>> getMyResolvedTickets(
            Pageable pageable) {

        return ResponseEntity.ok(
                ticketService.getResolvedTicketsByUser(
                        authService.getCurrentUser().getId(),
                        pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGER')")
    @GetMapping("/resolved/{id}")
    public ResponseEntity<Page<TicketResponse>> getResolvedTickets(
            @PathVariable Long id,
            Pageable pageable) {

        return ResponseEntity.ok(
                ticketService.getResolvedTicketsByUser(id, pageable));
    }

    @PreAuthorize("hasRole('IT_SUPPORT')")
    @GetMapping("/my-pending")
    public  ResponseEntity<Page<TicketResponse>> getUserPendingTickets(
            Pageable  pageable
    ){
        return ResponseEntity.ok(
                ticketService.getPendingTicketsByUser(
                        authService.getCurrentUser().getId(),
                        pageable));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<Page<TicketResponse>> getAllPendingTickets(
            Pageable pageable) {

        return ResponseEntity.ok(
                ticketService.getAllPendingTickets(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGER')")
    @GetMapping("/user/history/{userId}")
    public  ResponseEntity<Page<TicketResponse>> getTicketHistoryByUser(
            @PathVariable Long userId, Pageable pageable ){
        return ResponseEntity.ok(
                ticketService.getTicketHistoryByUser(
                        userId,
                         pageable
                )
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGER')")
    @GetMapping("/user/statistics/{userId}")
    public ResponseEntity<Map<String, Object>> getTicketStatisticsByUser(@PathVariable Long userId, Pageable pageable) {
        Map<String, Object> response = ticketService.getUserTicketStatistics(userId, pageable);
        return ResponseEntity.ok(response);
    }

 }
