package com.help.desk.tickets.dto.request;


import com.help.desk.tickets.enums.Category;
import com.help.desk.tickets.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateRequest {

    private String title;
    private String description;
    private Priority priority;
    private Category category;
    private LocalDateTime dueDate;
    private Long assignedToId;
}

