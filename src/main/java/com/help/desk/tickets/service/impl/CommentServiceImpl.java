package com.help.desk.tickets.service.impl;

import com.help.desk.exception.ResourceNotFoundException;
import com.help.desk.tickets.dto.request.CommentRequest;
import com.help.desk.tickets.dto.response.CommentResponse;
import com.help.desk.tickets.enums.CommentType;
import com.help.desk.tickets.model.Ticket;
import com.help.desk.tickets.model.TicketComment;
import com.help.desk.tickets.repository.TicketCommentRepository;
import com.help.desk.tickets.repository.TicketRepository;
import com.help.desk.tickets.service.CommentService;
import com.help.desk.tickets.service.HistoryService;
import com.help.desk.user.enums.UserRole;
import com.help.desk.user.model.User;
import com.help.desk.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final TicketCommentRepository ticketCommentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository  userRepository;
    private final HistoryService historyService;

    @Override
    public CommentResponse addComment(CommentRequest request) {
        // 1. Get ticket and user
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + request.getTicketId()));

        User commentedBy = userRepository.findById(request.getCommentedById())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getCommentedById()));

        // 2. Validate permissions
        validateCommentPermissions(ticket, commentedBy);

        // 3. Determine comment type
        CommentType commentType = request.getCommentType();
        if (commentType == null) {
            // If user is EMPLOYEE, comments are PUBLIC by default
            // If user is IT_SUPPORT or ADMIN, comments are INTERNAL by default
            if (commentedBy.getRole() == UserRole.EMPLOYEE) {
                commentType = CommentType.PUBLIC;
                request.setIsInternal(false);
            } else {
                commentType = CommentType.INTERNAL;
                request.setIsInternal(true);
            }
        }

        // 4. Create comment
        TicketComment comment = TicketComment.builder()
                .comment(request.getComment())
                .ticket(ticket)
                .commentedBy(commentedBy)
                .commentType(commentType)
                .isInternal(request.getIsInternal() != null ? request.getIsInternal() : false)
                .build();

        TicketComment savedComment = ticketCommentRepository.save(comment);

        // 5. Add history entry
        String historyComment = String.format("Comment added by %s (%s): %s",
                commentedBy.getFirstName(),
                commentedBy.getRole().name(),
                request.getComment().length() > 50 ?
                        request.getComment().substring(0, 50) + "..." :
                        request.getComment()
        );

        historyService.createHistoryEntry(
                ticket,
                commentedBy,
                ticket.getStatus(),
                ticket.getStatus(),
                historyComment
        );

        log.info("Comment added to ticket {} by user {} (Role: {})",
                ticket.getTicketNumber(),
                commentedBy.getFirstName(),
                commentedBy.getRole().name());

        return mapToResponse(savedComment, commentedBy);
    }

    @Override
    public List<CommentResponse> getCommentsByTicketId(Long ticketId , Long userId) {
        User  user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        List<TicketComment> comments = ticketCommentRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);

        return comments.stream()
                .filter(comment -> canViewComment(comment, user))
                .map(comment -> mapToResponse(comment, comment.getCommentedBy()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<CommentResponse> getCommentsByTicketId(Long ticketId, Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Page<TicketComment> commentPage = ticketCommentRepository.findByTicketId(ticketId, pageable);

        // Map to CommentResponse or null, then filter out nulls
        List<CommentResponse> filteredComments = commentPage.getContent().stream()
                .map(comment -> {
                    if (canViewComment(comment, user)) {
                        return mapToResponse(comment, comment.getCommentedBy());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Return as Page
        return new PageImpl<>(filteredComments, pageable, commentPage.getTotalElements());
    }

    @Override
    public List<CommentResponse> getCommentsByUserId(Long userId) {
        return ticketCommentRepository.findByCommentedByIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(comment -> mapToResponse(comment, comment.getCommentedBy()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getLatestComments(int limit, Long userId) {

        Pageable pageable = Pageable.ofSize(limit);

        return ticketCommentRepository.findLastestComments(userId,pageable)
                .stream()
                .map(comment -> mapToResponse(comment, comment.getCommentedBy()))
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse updateComment(Long commentId, String newComment, Long userId) {

        TicketComment comment = ticketCommentRepository.findById(commentId)
                .orElseThrow(() -> new  ResourceNotFoundException("Comment Not Found"+ commentId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"+ userId));

        if (!comment.getCommentedBy().getId().equals(userId) && user.getRole() != UserRole.ADMIN){
            throw new RuntimeException("You don't have permission to edit this comment");
        }

        String oldComment = comment.getComment();
        comment.setComment(newComment);
        TicketComment updatedComment = ticketCommentRepository.save(comment);

        historyService.createHistoryEntry(
                comment.getTicket(),
                user,
                comment.getTicket().getStatus(),
                comment.getTicket().getStatus(),
                String.format("Comment updated from: %s to: %s",
                        oldComment.length() > 30 ? oldComment.substring(0, 30) + "..." : oldComment,
                        newComment.length() > 30 ? newComment.substring(0, 30) + "..." : newComment)
        );

        log.info("Comment {} updated by user {}", commentId, user.getFirstName());

        return mapToResponse(updatedComment, user);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        TicketComment comment = ticketCommentRepository.findById(commentId)
                .orElseThrow(() -> new  ResourceNotFoundException("Comment Not Found"+ commentId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"+ userId));

        if (!comment.getCommentedBy().getId().equals(userId) && user.getRole() != UserRole.ADMIN){
            throw new RuntimeException("You don't have permission to edit this comment");
        }
        String deleteComment = comment.getComment();
        historyService.createHistoryEntry(
                comment.getTicket(),
                 user,
                comment.getTicket().getStatus(),
                comment.getTicket().getStatus(),
                String.format("Comment deleted: %s",
                        deleteComment.length() > 50 ? deleteComment.substring(0,50)+ "..." : deleteComment
                        )
        );
        log.info("Comment {} deleted by user {}", commentId, user.getFirstName());
    }

    @Override
    public List<CommentResponse> getPublicComments(Long ticketId) {

        return ticketCommentRepository.findPublicCommentsByTicketId(ticketId)
                .stream()
                .map(comment -> mapToResponse(comment, comment.getCommentedBy()))
                .collect(Collectors.toList());
    }

    @Override
    public long countCommentsByTicketId(Long ticketId) {
        return ticketCommentRepository.countByTicketId(ticketId);
    }

    @Override
    public List<CommentResponse> getInternalComments(Long ticketId) {

        return ticketCommentRepository.findByCommentedByIdOrderByCreatedAtDesc(ticketId)
                .stream()
                .map(comment ->  mapToResponse(comment, comment.getCommentedBy()))
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse convertToInternal(Long commentId, Long userId) {
        TicketComment comment = ticketCommentRepository.findById(commentId)
                .orElseThrow(() ->new ResourceNotFoundException("Comment Not Found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->new ResourceNotFoundException("User Not Found"));

        if(user.getRole() != UserRole.IT_SUPPORT && user.getRole() != UserRole.ADMIN){
            throw new RuntimeException("Only support team members can convert comments to internal");
        }
        comment.setIsInternal(true);
        comment.setCommentType(CommentType.INTERNAL);
        TicketComment updateComment = ticketCommentRepository.save(comment);

        return mapToResponse(updateComment, user);
    }

    // Helper Function
    private CommentResponse mapToResponse(TicketComment comment, User commentedBy) {
        // Determine if user can delete/edit
        boolean canDelete = false;
        boolean canEdit = false;

        // In real implementation, you'd pass the current user
        // For now, we'll check if the commenter is the same as the current user

        return CommentResponse.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .ticketId(comment.getTicket().getId())
                .ticketNumber(comment.getTicket().getTicketNumber())
                .ticketTitle(comment.getTicket().getTitle())
                .commentedById(commentedBy.getId())
                .commentedByName(commentedBy.getFirstName())
                .commentedByEmail(commentedBy.getEmail())
                .commentedByRole(commentedBy.getRole().name())
                .commentType(comment.getCommentType() != null ? comment.getCommentType() : CommentType.PUBLIC)
                .isInternal(comment.getIsInternal() != null ? comment.getIsInternal() : false)
                .createdAt(comment.getCreatedAt())
                .timeAgo(getTimeAgo(comment.getCreatedAt()))
                .canDelete(canDelete)
                .canEdit(canEdit)
                .build();
    }

    private String getTimeAgo(LocalDateTime dateTime){
        LocalDateTime now = LocalDateTime.now();

        long seconds = ChronoUnit.SECONDS.between(dateTime,now);

        if (seconds < 60 ){
            return seconds + "Second ago";
        }
        if (seconds < 3600 ){
            return  (seconds / 60) + " minutes ago";
        }

        if (seconds < 86400 ){
            return (seconds / 3600) + " hours ago";
        }
        if (seconds < 604800 ){
            return (seconds / 86400) + " days ago";
        }

        return (seconds / 604800) + " weeks ago";
    }

    // === PRIVATE HELPER METHODS ===

    private void validateCommentPermissions(Ticket ticket, User user) {
        UserRole role = user.getRole();

        switch (role) {
            case EMPLOYEE:
                // Employee can only comment on their own tickets
                if (!ticket.getCreatedBy().getId().equals(user.getId())) {
                    throw new RuntimeException("You can only comment on tickets you created");
                }
                break;

            case IT_SUPPORT:
                // Support can comment on tickets assigned to them
                if (ticket.getAssignedTo() != null &&
                        !ticket.getAssignedTo().getId().equals(user.getId())) {
                    // Support can also comment if they are the resolver
                    if (ticket.getResolvedBy() == null ||
                            !ticket.getResolvedBy().getId().equals(user.getId())) {
                        // Allow if ticket is not assigned to anyone (unassigned tickets)
                        if (ticket.getAssignedTo() != null) {
                            throw new RuntimeException("You can only comment on tickets assigned to you");
                        }
                    }
                }
                break;

            case ADMIN:
                // Admin can comment on any ticket
                break;

            default:
                throw new RuntimeException("Unknown user role: " + role);
        }
    }
    private boolean canViewComment(TicketComment comment, User user) {
        // If comment is public, everyone can view
        if (!comment.getIsInternal() && comment.getCommentType() == CommentType.PUBLIC) {
            return true;
        }

        // If comment is internal, only support team and admin can view
        if (comment.getIsInternal() || comment.getCommentType() == CommentType.INTERNAL) {
            return user.getRole() == UserRole.IT_SUPPORT || user.getRole() == UserRole.ADMIN;
        }

        // System comments visible to everyone
        if (comment.getCommentType() == CommentType.SYSTEM) {
            return true;
        }

        return false;
    }

}
