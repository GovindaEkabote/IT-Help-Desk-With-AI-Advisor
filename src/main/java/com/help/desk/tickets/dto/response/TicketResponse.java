package com.help.desk.tickets.dto.response;

import com.help.desk.tickets.enums.Category;
import com.help.desk.tickets.enums.Priority;
import com.help.desk.tickets.enums.Source;
import com.help.desk.tickets.enums.Status;
import com.help.desk.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String ticketNumber;
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private Status status;
    private Source source;

    // User details as primitives instead of full User objects
    private Long createdById;
    private String createdByName;
    private Long assignedToId;
    private String assignedToName;
    private Long resolvedById;
    private String resolvedByName;

    private LocalDateTime resolvedAt;
    private LocalDateTime dueDate;
    private Integer escalationLevel;
    private Integer satisfactionRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional useful fields
    private Integer commentCount;
    private Integer historyCount;
    private Boolean isOverdue;
    private String timeInStatus;

}
