package com.help.desk.tickets.dto.response;

public class UserTicketStatsResponse {

    private Long userId;
    private String userName;

    // Tickets created by user
    private Long totalCreated;
    private Long createdOpen;
    private Long createdResolved;
    private Long createdCancelled;

    // Tickets assigned to user
    private Long totalAssigned;
    private Long assignedOpen;
    private Long assignedInProgress;
    private Long assignedResolved;
    private Long assignedOverdue;

    // Tickets resolved by user
    private Long totalResolved;
    private Double averageResolutionTimeHours;
    private Double averageSatisfactionRating;

    // Performance
    private Integer pendingTicketsCount;
    private Integer escalatedTicketsCount;
    private Long ticketsResolvedThisWeek;
    private Long ticketsResolvedThisMonth;
}
