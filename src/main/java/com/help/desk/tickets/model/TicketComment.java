package com.help.desk.tickets.model;

import com.help.desk.tickets.enums.CommentType;
import com.help.desk.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commented_by")
    private User commentedBy;

    // FIX: Changed from @ManyToOne to @Enumerated
    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type")
    private CommentType commentType;

    @Column(name = "is_internal")
    private Boolean isInternal = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isInternal == null) {
            this.isInternal = false;
        }
        if (this.commentType == null) {
            this.commentType = CommentType.PUBLIC;
        }
    }
}