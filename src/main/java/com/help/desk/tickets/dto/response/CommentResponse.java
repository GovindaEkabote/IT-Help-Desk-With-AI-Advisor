package com.help.desk.tickets.dto.response;

import com.help.desk.tickets.enums.CommentType;
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
    private String commentedByRole;
    private CommentType commentType;
    private Boolean isInternal;
    private LocalDateTime createdAt;
    private String timeAgo;
    private Boolean canDelete;
    private Boolean canEdit;
}
