package com.help.desk.tickets.service.impl;

import com.help.desk.tickets.dto.request.TicketHistoryRepository;
import com.help.desk.tickets.dto.response.TicketHistoryResponse;
import com.help.desk.tickets.enums.Status;
import com.help.desk.tickets.model.Ticket;
import com.help.desk.tickets.model.TicketHistory;
import com.help.desk.tickets.service.HistoryService;
import com.help.desk.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional
public class HistoryServiceImpl implements HistoryService {

    private final TicketHistoryRepository historyRepository;

    @Override
    public TicketHistoryResponse createHistoryEntry(Ticket ticket, User changedBy, Status oldStatus, Status newStatus, String comment) {
        TicketHistory history = TicketHistory.builder()
                .ticket(ticket)
                .changedBy(changedBy)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .comments(comment)
                .build();

        TicketHistory savedHistory = historyRepository.save(history);
        return mapToResponse(savedHistory);
    }

    @Override
    public List<TicketHistoryResponse> getHistoryByTicketId(Long ticketId) {
        return List.of();
    }

    @Override
    public Page<TicketHistoryResponse> getHistoryByTicketId(Long ticketId, Pageable pageable) {
        return null;
    }

    @Override
    public List<TicketHistoryResponse> getHistoryByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<TicketHistoryResponse> getHistoryByStatusChange(Status oldStatus, Status newStatus) {
        return List.of();
    }

    @Override
    public Map<Status, Long> getStatusChangeStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return Map.of();
    }

    @Override
    public List<TicketHistoryResponse> getTicketTimeline(Long ticketId) {
        return List.of();
    }

    private TicketHistoryResponse mapToResponse(TicketHistory history) {
        String statusChangeDesc = "";
        if (history.getOldStatus() != null && history.getNewStatus() != null) {
            statusChangeDesc = String.format("Changed from %s to %s",
                    history.getOldStatus().name(), history.getNewStatus().name());
        } else if (history.getNewStatus() != null) {
            statusChangeDesc = String.format("Set to %s", history.getNewStatus().name());
        }

        return TicketHistoryResponse.builder()
                .id(history.getId())
                .ticketId(history.getTicket().getId())
                .ticketNumber(history.getTicket().getTicketNumber())
                .ticketTitle(history.getTicket().getTitle())
                .changedById(history.getChangedBy().getId())
                .changedByName(history.getChangedBy().getFirstName())
                .changedByEmail(history.getChangedBy().getEmail())
                .oldStatus(history.getOldStatus())
                .newStatus(history.getNewStatus())
                .statusChangeDescription(statusChangeDesc)
                .comments(history.getComments())
                .changedAt(history.getChangedAt())
                .timeAgo(getTimeAgo(history.getChangedAt()))
                .build();
    }
    private String getTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(dateTime, now);

        if (seconds < 60) return seconds + " seconds ago";
        if (seconds < 3600) return (seconds / 60) + " minutes ago";
        if (seconds < 86400) return (seconds / 3600) + " hours ago";
        if (seconds < 604800) return (seconds / 86400) + " days ago";
        return (seconds / 604800) + " weeks ago";
    }
}
