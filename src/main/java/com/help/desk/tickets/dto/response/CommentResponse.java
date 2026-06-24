package com.help.desk.tickets.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String comment;
    private Long ticketId;
    private String ticketNumber;
    private String ticketTitle;
    private Long commentedById;
    private String commentedByName;
    private String commentedByEmail;
    private LocalDateTime createdAt;
    private String timeAgo;
}
