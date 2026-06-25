package com.help.desk.tickets.dto.request;

import com.help.desk.tickets.enums.CommentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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

    @NotNull(message = "Ticket ID is required")
    @Positive(message = "Ticket ID must be positive")
    private Long ticketId;

    @NotNull(message = "Commented By ID is required")
    @Positive(message = "Commented By ID must be positive")
    private Long commentedById;

    private CommentType commentType;

    @Builder.Default
    private Boolean isInternal = false;
}