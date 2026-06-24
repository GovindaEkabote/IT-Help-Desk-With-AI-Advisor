package com.help.desk.tickets.service.impl;

import com.help.desk.tickets.dto.request.CommentRequest;
import com.help.desk.tickets.dto.response.CommentResponse;
import com.help.desk.tickets.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    @Override
    public CommentResponse addComment(CommentRequest commentRequest) {
        return null;
    }

    @Override
    public List<CommentResponse> getCommentsByTicketId(Long ticketId) {
        return List.of();
    }

    @Override
    public Page<CommentResponse> getCommentsByTicketId(Long ticketId, Pageable pageable) {
        return null;
    }

    @Override
    public List<CommentResponse> getCommentsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<CommentResponse> getLatestComments(int limit) {
        return List.of();
    }

    @Override
    public CommentResponse updateComment(Long commentId, String newComment) {
        return null;
    }

    @Override
    public void deleteComment(Long commentId) {

    }

    @Override
    public long countCommentsByTicketId(Long ticketId) {
        return 0;
    }
}
