package com.help.desk.tickets.service.impl;


import com.help.desk.auth.service.AuthService;
import com.help.desk.exception.ResourceNotFoundException;
import com.help.desk.tickets.dto.request.TicketRequest;
import com.help.desk.tickets.dto.response.TicketResponse;
import com.help.desk.tickets.dto.response.TicketStatisticsResponse;
import com.help.desk.tickets.enums.Priority;
import com.help.desk.tickets.enums.Status;
import com.help.desk.tickets.model.Ticket;
import com.help.desk.tickets.repository.TicketRepository;
import com.help.desk.tickets.service.TicketService;
import com.help.desk.user.enums.UserRole;
import com.help.desk.user.model.User;
import com.help.desk.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service

public class TicketServiceImpl  implements TicketService {

    private TicketRepository ticketRepository;
    private UserRepository userRepository;
    private AuthService authService;


    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository,AuthService authService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public TicketResponse createTicket(TicketRequest request) {
        User currentUser = authService.getCurrentUser();

        User assignedTo = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned Not Found"));


        Ticket ticket = Ticket.builder()
                .ticketNumber(generateTicketNumber())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(Priority.valueOf(request.getPriority()))
                .status(Status.NEW)
                .source(request.getSource())
                .createdBy(currentUser)
                .assignedTo(assignedTo)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        return mapToResponse(savedTicket);
    }

    @Override
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        User currentUser = authService.getCurrentUser();

        UserRole role = currentUser.getRole();

        boolean hasAccess =
                        role == UserRole.SUPER_ADMIN ||
                        role == UserRole.ADMIN ||
                        role == UserRole.IT_SUPPORT ||
                        ticket.getCreatedBy().getId().equals(currentUser.getId());

        if (!hasAccess){
            throw new AccessDeniedException("You are not authorized to view this ticket");
        }
        return mapToResponse(ticket);
    }

    @Override
    public TicketResponse getTicketByNumber(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        User user = authService.getCurrentUser();

        UserRole role = user.getRole();

        boolean hasAccess =
                role == UserRole.SUPER_ADMIN ||
                        role == UserRole.ADMIN ||
                        role == UserRole.IT_SUPPORT;
        if(!hasAccess){
            throw new AccessDeniedException("You are not authorized to view this ticket");
        }
        return mapToResponse(ticket);
    }

    @Override
    public TicketResponse updateTicket(Long id, TicketRequest ticketRequest) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));
        User currentUser = authService.getCurrentUser();

        boolean hasAccess =
                ticket.getCreatedBy().getId().equals(currentUser.getId());

        if (!hasAccess) {
            throw new AccessDeniedException("You are not authorized to update this ticket");
        }

        ticket.setTitle(ticketRequest.getTitle());
        ticket.setDescription(ticketRequest.getDescription());
        ticket.setCategory(ticketRequest.getCategory());
        ticket.setPriority(Priority.valueOf(ticketRequest.getPriority()));
        ticket.setUpdatedAt(LocalDateTime.now());

        if (ticketRequest.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(ticketRequest.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned Not Found"));
            ticket.setAssignedTo(assignedTo);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToResponse(updatedTicket);
    }

    @Override
    public void deleteTicket(Long id) {
        if(!ticketRepository.existsById(id)){
            throw new ResourceNotFoundException("Ticket Not Found");
        }
        ticketRepository.deleteById(id);
    }

    @Override
    public TicketResponse assignTicket(Long id, Long userId) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        if (assignee.getRole() != UserRole.IT_SUPPORT) {
            throw new IllegalArgumentException(
                    "Ticket can only be assigned to IT_SUPPORT users");
        }

        if (ticket.getStatus() == Status.RESOLVED ||
                ticket.getStatus() == Status.CLOSED) {
            throw new IllegalStateException(
                    "Resolved/Closed tickets cannot be reassigned");
        }

        if (ticket.getAssignedTo() != null &&
                ticket.getAssignedTo().getId().equals(userId)) {
            throw new IllegalStateException(
                    "Ticket is already assigned to this user");
        }

        ticket.setAssignedTo(assignee);
        ticket.setStatus(Status.ASSIGNED);
        ticket.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse claimTicket(Long id) {
        User currentUser = authService.getCurrentUser();

        if(currentUser.getRole() != UserRole.IT_SUPPORT){
            throw new AccessDeniedException("You are not authorized to claim this ticket");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));
        if (ticket.getAssignedTo() != null){
            throw new IllegalStateException("Ticket is already assigned");
        }
        ticket.setAssignedTo(currentUser);
        ticket.setStatus(Status.ASSIGNED);
        ticket.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse resolveTicket(Long id, Long userId) {
        User currentUser = authService.getCurrentUser();

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        User resolver = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resolver Not Found"));

        boolean canResolve =
                currentUser.getRole() == UserRole.IT_SUPPORT ||
                currentUser.getRole() == UserRole.ADMIN ||
                currentUser.getRole() == UserRole.SUPER_ADMIN;

        if (!canResolve) {
            throw new AccessDeniedException("You are not authorized to resolve this ticket");
        }

        if(currentUser.getRole() == UserRole.IT_SUPPORT &&
                (ticket.getAssignedTo() == null ||
                        !ticket.getAssignedTo().getId().equals(currentUser.getId()))
        ){
            throw new AccessDeniedException("You are not authorized to resolve this ticket");
        }

        ticket.setStatus(Status.RESOLVED);
        ticket.setResolvedBy(resolver);
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(ticketRepository.save(ticket));

    }

    @Override
    public TicketResponse updateTicketStatus(Long id, String status) {
        User currentUser = authService.getCurrentUser();

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        boolean canUpdate =
                currentUser.getRole() == UserRole.IT_SUPPORT ||
                        currentUser.getRole() == UserRole.ADMIN ||
                        currentUser.getRole() == UserRole.SUPER_ADMIN ||
                        currentUser.getRole() == UserRole.MANAGER;

        if (!canUpdate) {
            throw new AccessDeniedException("You are not allowed to update ticket status");
        }

        Status newStatus;
        try {
            newStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        // optional safety rules
        if (ticket.getStatus() == Status.RESOLVED && newStatus != Status.CLOSED) {
            throw new IllegalStateException("Resolved ticket can only be closed");
        }

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setResolvedBy(currentUser);
        ticket.setResolvedAt(LocalDateTime.now());

        return mapToResponse(ticketRepository.save(ticket));
    }

    @Override
    public Page<TicketResponse> getTicketsByCreatedById(Pageable pageable) {
        User user = authService.getCurrentUser();

        return ticketRepository.findByCreatedById(user.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<TicketResponse> getResolvedTicketsByUser( Long userId,Pageable pageable) {

        User currentUser = authService.getCurrentUser();

        boolean hasAccess =
                currentUser.getRole() == UserRole.SUPER_ADMIN ||
                        currentUser.getRole() == UserRole.ADMIN ||
                        currentUser.getRole() == UserRole.MANAGER ||
                        currentUser.getId().equals(userId);

        if (!hasAccess) {
            throw new AccessDeniedException(
                    "You are not authorized to view these tickets");
        }

        return ticketRepository
                .findByCreatedByIdAndStatus(
                        userId,
                        Status.CLOSED,
                        pageable)
                .map(this::mapToResponse);
    }


    @Override
    public Page<TicketResponse> getPendingTicketsByUser(Long userId, Pageable pageable) {
        User user = authService.getCurrentUser();

        boolean hasAccess =
                user.getRole() == UserRole.SUPER_ADMIN ||
                        user.getRole() == UserRole.ADMIN ||
                        user.getRole() == UserRole.MANAGER ||
                        user.getId().equals(userId);

        if (!hasAccess) {
            throw new AccessDeniedException(
                    "You are not authorized to view these tickets");
        }

        return ticketRepository.findByAssignedToIdAndStatus(
                userId,
                Status.NEW,
                pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<TicketResponse> getAllPendingTickets(Pageable pageable) {

        return ticketRepository
                .findByStatus(Status.NEW, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<TicketResponse> getTicketHistoryByUser(Long userId, Pageable pageable) {
        User user = authService.getCurrentUser();

        boolean hasAccess =
                user.getRole() == UserRole.MANAGER ||
                        user.getRole() == UserRole.ADMIN ||
                        user.getRole() == UserRole.SUPER_ADMIN ||
                        user.getId().equals(userId);

        if (!hasAccess){
            throw new AccessDeniedException(
                    "You are not authorized to view these tickets");
        }
        return ticketRepository.findByCreatedById(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Map<String, Object> getUserTicketStatistics(Long userId, Pageable pageable) {

        // Authorization check
        User currentUser = authService.getCurrentUser();
        boolean hasAccess = currentUser.getRole() == UserRole.SUPER_ADMIN ||
                currentUser.getRole() == UserRole.ADMIN ||
                currentUser.getRole() == UserRole.MANAGER ||
                currentUser.getId().equals(userId);

        if (!hasAccess) {
            throw new AccessDeniedException("You are not authorized to view statistics");
        }

        // Fetch tickets
        Page<Ticket> ticketPage = ticketRepository.findByCreatedById(userId, pageable);
        List<Ticket> tickets = ticketPage.getContent();

        // Calculate total tickets from the page
        long totalTickets = ticketPage.getTotalElements();

        // =========================
        // PROCESS TICKETS ONCE - More efficient!
        // =========================
        long newTickets = 0;
        long inProgressTickets = 0;
        long resolvedTickets = 0;
        long closedTickets = 0;
        long escalatedTickets = 0;

        Map<String, Long> ticketsByPriority = new HashMap<>();
        Map<String, Long> ticketsByCategory = new HashMap<>();
        Map<String, Long> ticketsBySource = new HashMap<>();

        List<Long> resolutionTimes = new ArrayList<>();
        List<Integer> satisfactionRatings = new ArrayList<>();

        for (Ticket ticket : tickets) {
            // Count by status
            switch (ticket.getStatus()) {
                case NEW -> newTickets++;
                case IN_PROGRESS -> inProgressTickets++;
                case RESOLVED -> resolvedTickets++;
                case CLOSED -> closedTickets++;
                case ESCALATED -> escalatedTickets++;
            }

            // Group by priority
            ticketsByPriority.merge(ticket.getPriority().name(), 1L, Long::sum);

            // Group by category
            ticketsByCategory.merge(ticket.getCategory().name(), 1L, Long::sum);

            // Group by source
            ticketsBySource.merge(ticket.getSource().name(), 1L, Long::sum);

            // Collect resolution times
            if (ticket.getResolvedAt() != null) {
                resolutionTimes.add(Duration.between(ticket.getCreatedAt(), ticket.getResolvedAt()).toHours());
            }

            // Collect satisfaction ratings
            if (ticket.getSatisfactionRating() != null) {
                satisfactionRatings.add(ticket.getSatisfactionRating());
            }
        }

        // =========================
        // CALCULATE METRICS
        // =========================
        long successTickets = resolvedTickets + closedTickets;
        double resolutionRate = totalTickets == 0 ? 0.0 : ((double) successTickets / totalTickets) * 100;

        double avgResolutionTimeHours = resolutionTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        double satisfactionRate = satisfactionRatings.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        // =========================
        // BUILD RESPONSE
        // =========================
        TicketStatisticsResponse response = TicketStatisticsResponse.builder()
                .period("ALL")
                .periodLabel("All time")
                .startDate(null)
                .endDate(null)
                .totalTickets(totalTickets)
                .newTickets(newTickets)
                .inProgressTickets(inProgressTickets)
                .resolvedTickets(resolvedTickets)
                .closedTickets(closedTickets)
                .escalatedTickets(escalatedTickets)  // Now uses calculated value
                .resolutionRate(resolutionRate)
                .averageResolutionTimeHours(avgResolutionTimeHours)
                .satisfactionRate(satisfactionRate)
                .ticketsByPriority(ticketsByPriority)
                .ticketsByCategory(ticketsByCategory)
                .ticketsBySource(ticketsBySource)
                // Not implemented yet (safe defaults)
                .averageResponseTimeHours(0.0)
                .overdueTickets(0L)
                .totalSatisfactionScore(satisfactionRatings.stream().mapToInt(Integer::intValue).sum())
                .totalSatisfactionRatings((long) satisfactionRatings.size())
                .ticketsByAssignee(Collections.emptyMap())
                .ticketsByCreator(Collections.emptyMap())
                .build();

        return Map.of(
                "success", true,
                "data", response
        );
    }

    @Override
    public Page<TicketResponse> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<TicketResponse> getTicketsByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        if (startDate == null){
            startDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        }
        if (endDate == null){
            endDate = LocalDateTime.now();
        }
        return ticketRepository.findTicketsByDateRange(startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public TicketStatisticsResponse getDailyStatistics(LocalDateTime date) {

        // If date is null, use today
        if (date == null) {
            date = LocalDateTime.now();
        }

        // Set to start and end of day
        LocalDateTime startOfDay = date.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = date.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return calculateStatistics(startOfDay, endOfDay, "DAY", date.toString());
    }

    @Override
    public List<TicketStatisticsResponse> getDailyStatisticsForRange(LocalDateTime startDate, LocalDateTime endDate) {
        return List.of();
    }

    @Override
    public TicketStatisticsResponse getWeeklyStatistics(int year, int week) {
        return null;
    }

    @Override
    public List<TicketStatisticsResponse> getWeeklyStatisticsForRange(int startYear, int startWeek, int endYear, int endWeek) {
        return List.of();
    }

    @Override
    public TicketStatisticsResponse getMonthlyStatistics(int year, int month) {
        return null;
    }

    @Override
    public List<TicketStatisticsResponse> getMonthlyStatisticsForRange(int startYear, int startMonth, int endYear, int endMonth) {
        return List.of();
    }

    @Override
    public List<TicketStatisticsResponse> getLastSixMonthsStatistics() {
        return List.of();
    }

    @Override
    public Map<String, Object> getAdminDashboardSummary() {
        return Map.of();
    }


//     Helper Methods

    private String generateTicketNumber() {
        return "TK-" + System.currentTimeMillis() ;
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .category(ticket.getCategory())
                .createdById(
                        ticket.getCreatedBy() != null
                                ? ticket.getCreatedBy().getId()
                                : null
                )
                .createdByName(
                        ticket.getCreatedBy() != null
                                ? ticket.getCreatedBy().getFirstName()
                                : null
                )

                .assignedToId(
                        ticket.getAssignedTo() != null
                                ? ticket.getAssignedTo().getId()
                                : null
                )
                .assignedToName(
                        ticket.getAssignedTo() != null
                                ? ticket.getAssignedTo().getFirstName()
                                : null
                )

                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    private TicketStatisticsResponse calculateStatistics(LocalDateTime startDate, LocalDateTime endDate,
                                                         String periodType, String periodLabel) {
        // Get tickets in date range
        Page<Ticket> ticketPage = ticketRepository.findTicketsByDateRange(startDate, endDate, Pageable.unpaged());
        List<Ticket> tickets = ticketPage.getContent();

        // Initialize counters
        long totalTickets = tickets.size();
        long newTickets = 0;
        long assignedTickets = 0;
        long inProgressTickets = 0;
        long resolvedTickets = 0;
        long closedTickets = 0;
        long escalatedTickets = 0;

        Map<String, Long> ticketsByPriority = new HashMap<>();
        Map<String, Long> ticketsByCategory = new HashMap<>();
        Map<String, Long> ticketsBySource = new HashMap<>();
        Map<String, Long> ticketsByAssignee = new HashMap<>();
        Map<String, Long> ticketsByCreator = new HashMap<>();

        List<Long> resolutionTimes = new ArrayList<>();
        List<Integer> satisfactionRatings = new ArrayList<>();

        // Process all tickets
        for (Ticket ticket : tickets) {
            // Count by status
            switch (ticket.getStatus()) {
                case NEW -> newTickets++;
                case ASSIGNED -> assignedTickets++;
                case IN_PROGRESS -> inProgressTickets++;
                case RESOLVED -> resolvedTickets++;
                case CLOSED -> closedTickets++;
                case ESCALATED -> escalatedTickets++;
            }

            // Group by priority
            ticketsByPriority.merge(ticket.getPriority().name(), 1L, Long::sum);

            // Group by category
            if (ticket.getCategory() != null) {
                ticketsByCategory.merge(ticket.getCategory().name(), 1L, Long::sum);
            }

            // Group by source
            if (ticket.getSource() != null) {
                ticketsBySource.merge(ticket.getSource().name(), 1L, Long::sum);
            }

            // Group by assignee
            if (ticket.getAssignedTo() != null) {
                ticketsByAssignee.merge(ticket.getAssignedTo().getFirstName(), 1L, Long::sum);
            }

            // Group by creator
            if (ticket.getCreatedBy() != null) {
                ticketsByCreator.merge(ticket.getCreatedBy().getFirstName(), 1L, Long::sum);
            }

            // Resolution time
            if (ticket.getResolvedAt() != null && ticket.getCreatedAt() != null) {
                long hours = Duration.between(ticket.getCreatedAt(), ticket.getResolvedAt()).toHours();
                resolutionTimes.add(hours);
            }

            // Satisfaction rating
            if (ticket.getSatisfactionRating() != null) {
                satisfactionRatings.add(ticket.getSatisfactionRating());
            }
        }

        // Calculate metrics
        long openTickets = newTickets + assignedTickets + inProgressTickets;
        long closedOrResolved = resolvedTickets + closedTickets;
        double resolutionRate = totalTickets == 0 ? 0.0 : ((double) closedOrResolved / totalTickets) * 100;

        double avgResolutionTime = resolutionTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        double satisfactionRate = satisfactionRatings.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        // Build response
        return TicketStatisticsResponse.builder()
                .period(periodType)
                .periodLabel(periodLabel)
                .startDate(startDate)
                .endDate(endDate)
                .totalTickets(totalTickets)
                .newTickets(newTickets)
                .inProgressTickets(inProgressTickets)
                .resolvedTickets(resolvedTickets)
                .closedTickets(closedTickets)
                .escalatedTickets(escalatedTickets)
                .openTickets(openTickets)
                .resolutionRate(resolutionRate)
                .averageResolutionTimeHours(avgResolutionTime)
                .satisfactionRate(satisfactionRate)
                .ticketsByPriority(ticketsByPriority)
                .ticketsByCategory(ticketsByCategory)
                .ticketsBySource(ticketsBySource)
                .ticketsByAssignee(ticketsByAssignee)
                .ticketsByCreator(ticketsByCreator)
                .totalSatisfactionScore(satisfactionRatings.stream().mapToInt(Integer::intValue).sum())
                .totalSatisfactionRatings((long) satisfactionRatings.size())
                .averageResponseTimeHours(0.0) // You can implement this separately
                .overdueTickets(0L) // You can implement this separately
                .build();
    }

    private Map<String, Long> getTicketsGroupedByPriority() {
        Map<String, Long> priorityCounts = new HashMap<>();
        for (Priority priority : Priority.values()) {
            long count = ticketRepository.findAll().stream()
                    .filter(t -> t.getPriority() == priority)
                    .count();
            priorityCounts.put(priority.name(), count);
        }
        return priorityCounts;
    }

    private Map<String, Long> getTicketsGroupedByCategory() {
        // This would be more efficient with a custom query
        // For now, let's get all tickets and group
        Map<String, Long> categoryCounts = new HashMap<>();
        List<Ticket> allTickets = ticketRepository.findAll();

        for (Ticket ticket : allTickets) {
            if (ticket.getCategory() != null) {
                categoryCounts.merge(ticket.getCategory().name(), 1L, Long::sum);
            }
        }
        return categoryCounts;
    }

    private Double calculateAverageResponseTime() {
        // This would typically be a custom query in your repository
        // For now, return a default value
        return 0.0;
    }


}