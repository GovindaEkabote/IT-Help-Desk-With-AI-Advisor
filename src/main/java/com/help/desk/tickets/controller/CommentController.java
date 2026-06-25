package com.help.desk.tickets.controller;

import com.help.desk.tickets.dto.request.CommentRequest;
import com.help.desk.tickets.dto.response.CommentResponse;
import com.help.desk.tickets.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @Valid @RequestBody CommentRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(request));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTicket(
            @PathVariable Long ticketId,
            @RequestParam Long userId
    ){
        return ResponseEntity.ok(commentService.getCommentsByTicketId(ticketId, userId));
    }

    @GetMapping("ticket/{ticketId}/paginated")
    public ResponseEntity<Page<CommentResponse>> getCommentsByTicketPaginated(
            @PathVariable Long ticketId,
            @RequestParam Long userId,
            Pageable pageable
    ){
        return ResponseEntity.ok(commentService.getCommentsByTicketId(ticketId, userId, pageable));
    }

    @GetMapping("/ticket/{ticketId}/internal")
    public ResponseEntity<List<CommentResponse>> getInternalComments(
            @PathVariable Long ticketId
    ){
        return ResponseEntity.ok(commentService.getInternalComments(ticketId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId));
    }

    @GetMapping("/latest/{userId}")
    public ResponseEntity<List<CommentResponse>> getLatestComments(@RequestParam(defaultValue = "10") int limit,@PathVariable Long userId) {
        return ResponseEntity.ok(commentService.getLatestComments(limit, userId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestParam String newComment,
            @RequestParam Long userId) {
        return ResponseEntity.ok(commentService.updateComment(commentId, newComment, userId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/convert-internal")
    public ResponseEntity<CommentResponse> convertToInternal(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(commentService.convertToInternal(commentId, userId));
    }

    @GetMapping("/ticket/{ticketId}/count")
    public ResponseEntity<Long> countComments(@PathVariable Long ticketId) {
        return ResponseEntity.ok(commentService.countCommentsByTicketId(ticketId));
    }

}
