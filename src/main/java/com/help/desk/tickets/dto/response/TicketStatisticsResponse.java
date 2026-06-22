package com.help.desk.tickets.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatisticsResponse {

    private String period; // "DAILY", "WEEKLY", "MONTHLY"
    private String periodLabel; // "2024-01-15", "Week 3, 2024", "January 2024"
    private LocalDateTime startDate;
    private LocalDateTime endDate;


    // Counts
    private Long totalTickets;
    private Long newTickets;
    private Long inProgressTickets;
    private Long resolvedTickets;
    private Long closedTickets;
    private Long cancelledTickets;

    // By priority
    private Map<String, Long> ticketsByPriority;
    private Map<String, Long> ticketsByCategory;
    private Map<String, Long> ticketsBySource;

    // Resolution metrics
    private Double averageResolutionTimeHours;
    private Double averageResponseTimeHours;
    private Long escalatedTickets;
    private Long overdueTickets;

    // Performance metrics
    private Double resolutionRate; // Percentage
    private Double satisfactionRate; // Average rating
    private Integer totalSatisfactionScore;
    private Long totalSatisfactionRatings;

    // User metrics
    private Map<String, Long> ticketsByAssignee;
    private Map<String, Long> ticketsByCreator;
}
