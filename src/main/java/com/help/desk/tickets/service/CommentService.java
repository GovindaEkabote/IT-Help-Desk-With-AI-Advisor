package com.help.desk.tickets.service;

import com.help.desk.tickets.dto.request.CommentRequest;
import com.help.desk.tickets.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {

    // Create comment
    CommentResponse addComment(CommentRequest commentRequest);

    // Get comments for a ticket
    List<CommentResponse>  getCommentsByTicketId(Long ticketId);
    Page<CommentResponse> getCommentsByTicketId(Long ticketId, Pageable  pageable);

    // Get comments by user
    List<CommentResponse> getCommentsByUserId(Long userId);

    // Get latest comments for dashboard
    List<CommentResponse> getLatestComments(int limit);

    // Update comment
    CommentResponse updateComment(Long commentId, String newComment);

    // Delete comment
    void deleteComment(Long commentId);

    // Count comments for a ticket
    long countCommentsByTicketId(Long ticketId);
}
