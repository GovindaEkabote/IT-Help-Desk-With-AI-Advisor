package com.help.desk.tickets.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Comment text is required")
    private String comment;

    @NotBlank(message = "Ticket ID is required")
    private Long ticketId;

    private Long commentedById;
}
