package com.help.desk.tickets.model;

import com.help.desk.tickets.enums.Status;
import com.help.desk.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private Status oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private Status newStatus;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @PrePersist
    public void onCreate() {
        this.changedAt = LocalDateTime.now();
    }
}