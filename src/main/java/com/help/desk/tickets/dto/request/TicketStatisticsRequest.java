package com.help.desk.tickets.dto.request;

import java.time.LocalDateTime;

public class TicketStatisticsRequest {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String period; // DAILY, WEEKLY, MONTHLY
    private Long userId; // For user-specific statistics
    private String status;
    private String priority;
    private String category;
}
