package com.help.desk.tickets.repository;

import com.help.desk.tickets.enums.Status;
import com.help.desk.tickets.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    Page<Ticket> findByCreatedById(Long userId, Pageable pageable);


    Page<Ticket> findByAssignedToId(Long userId, Pageable pageable);

    List<Ticket> findByStatus(Status status);

    long countByStatus(Status status);

    Page<Ticket> findByCreatedByIdAndStatus(
            Long userId,
            Status status,
            Pageable pageable);

    Page<Ticket> findByAssignedToIdAndStatus(
            Long assignedToId,
            Status status,
            Pageable pageable);

    Page<Ticket> findByStatus(
            Status status,
            Pageable pageable);

    // Counts for user statistics
    long countByCreatedById(Long userId);
    long countByAssignedToId(Long userId);
    long countByResolvedById(Long userId);
    long countByCreatedByIdAndStatus(Long userId, Status status);
    long countByAssignedToIdAndStatus(Long userId, Status status);
    @Query("SELECT t FROM Ticket t WHERE t.createdBy.id = :userId")
    List<Ticket> findAllByCreatedById(Long userId);


    @Query("""
    SELECT t FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate
    """)
    Page<Ticket> findTicketsByDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);


    @Query("""
        SELECT COUNT(t) FROM Ticket t
        WHERE t.createdAt BETWEEN :startDate AND :endDate
    """)
    Long countTicketsByDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);


    @Query("""
        SELECT COUNT(t) FROM Ticket t
        WHERE t.createdAt BETWEEN :resolvedAt AND :endDate
    """)
    Long countResolvedTicketsByDateRange(@Param("resolvedAt") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, t.createdAt, t.resolvedAt)) FROM Ticket t "
            +
           "WHERE t.resolvedAt BETWEEN :startDate AND :endDate AND t.status = 'RESOLVED'")
    Double getAverageResolutionTime(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.escalationLevel > 0 AND t.createdAt BETWEEN :startDate AND :endDate")
    Long countEscalatedTickets(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);


}

