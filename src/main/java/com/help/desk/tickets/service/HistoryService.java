package com.help.desk.tickets.service;

import com.help.desk.tickets.dto.response.TicketHistoryResponse;
import com.help.desk.tickets.enums.Status;
import com.help.desk.tickets.model.Ticket;
import com.help.desk.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface HistoryService {

    // Create history entry
    TicketHistoryResponse  createHistoryEntry(
            Ticket ticket,
            User changedBy,
            Status oldStatus,
            Status newStatus,
            String comment
    );
    // Get history for a ticket
    List<TicketHistoryResponse> getHistoryByTicketId(Long ticketId);
    Page<TicketHistoryResponse> getHistoryByTicketId(Long ticketId, Pageable  pageable);

    // Get history by user
    List<TicketHistoryResponse> getHistoryByUserId(Long userId);

    // Get history by status change
    List<TicketHistoryResponse> getHistoryByStatusChange(Status oldStatus, Status newStatus);

    // Get status change statistics
    Map<Status, Long> getStatusChangeStatistics(LocalDateTime startDate, LocalDateTime endDate);

    // Get ticket timeline
    List<TicketHistoryResponse> getTicketTimeline(Long ticketId);

}
