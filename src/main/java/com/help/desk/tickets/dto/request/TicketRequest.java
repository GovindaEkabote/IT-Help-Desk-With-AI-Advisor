package com.help.desk.tickets.dto.request;

import com.help.desk.tickets.enums.Category;
import com.help.desk.tickets.enums.Source;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;


    private String priority;


    private Category category;

    private Source source;

    private LocalDateTime dueDate;

    private Long assignedToId;
}
