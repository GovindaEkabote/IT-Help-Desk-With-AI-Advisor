package com.help.desk.tickets.repository;

import com.help.desk.tickets.enums.Status;
import com.help.desk.tickets.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    Page<Ticket> findByCreatedById(Long userId, Pageable pageable);

    Page<Ticket> findByAssignedToId(Long userId, Pageable pageable);

    List<Ticket> findByStatus(Status status);

    long countByStatus(Status status);
}
