package com.help.desk.tickets.dto.response;

import com.help.desk.tickets.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketHistoryResponse {

    private Long id;
    private Long ticketId;
    private String ticketNumber;
    private String ticketTitle;
    private Long changedById;
    private String changedByName;
    private String changedByEmail;
    private Status oldStatus;
    private Status newStatus;
    private String statusChangeDescription; // "Changed from OPEN to IN_PROGRESS"
    private String comments;
    private LocalDateTime changedAt;
    private String timeAgo;
}
