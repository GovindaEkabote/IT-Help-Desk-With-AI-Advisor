package com.help.desk.tickets.repository;

import com.help.desk.tickets.model.TicketComment;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    // Get all comments for a ticket
    List<TicketComment> findByTicketIdOrderByCreatedAtDesc(Long ticketId);

    // Get paginated comments for a ticket
    Page<TicketComment> findByTicketId(Long ticketId, Pageable pageable);

    // Get comments by user
    List<TicketComment> findByCommentedByIdOrderByCreatedAtDesc(Long userId);

    long countByTicketId(Long ticketId);

    @Query("""
            SELECT tc from TicketComment tc 
            where tc.ticket.id = :ticketId ORDER BY tc.createdAt DESC
            """)
    List<TicketComment> findLastestComments(
            @Param("ticketId") Long ticketId, Pageable pageable
    );

    List<TicketComment> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
