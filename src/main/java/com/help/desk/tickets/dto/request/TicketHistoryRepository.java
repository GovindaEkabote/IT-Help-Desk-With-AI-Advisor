package com.help.desk.tickets.dto.request;

import com.help.desk.tickets.enums.Status;
import com.help.desk.tickets.model.TicketHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {

    // Get all history for a ticket
    List<TicketHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId);

    // Get paginated history for a ticket
    Page<TicketHistory> findByTicketId(Long ticketId, Pageable pageable);

    // Get history by user
    List<TicketHistory> findByChangedByIdOrderByChangedAtDesc(Long userId);

    // Get history by status change
    List<TicketHistory> findByOldStatusAndNewStatus(Status oldStatus, Status newStatus);

    // Get history between dates
    List<TicketHistory> findByChangedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Get status change count for a ticket
    long countByTicketId(Long ticketId);

    // Get last status change for a ticket
    @Query("SELECT th FROM TicketHistory th " +
            "WHERE th.ticket.id = :ticketId ORDER BY th.changedAt DESC")
    List<TicketHistory> findLastStatusChange(
            @Param("ticketId") Long ticketId,
            Pageable pageable
    );

    // Get status change statistics
    @Query("""
            SELECT th.newStatus, COUNT(th) FROM TicketHistory th
            WHERE th.changedAt BETWEEN :startDate AND :endDate GROUP BY th.newStatus
            """)
    List<Object[]> countStatusChangesByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
