package com.help.desk.knowledgebase.controller;


import com.help.desk.exception.ApiResponse;
import com.help.desk.knowledgebase.dto.ArticleSearchDTO;
import com.help.desk.knowledgebase.dto.request.ArticleRequest;
import com.help.desk.knowledgebase.dto.response.ArticleResponse;
import com.help.desk.knowledgebase.service.KnowledgeBaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kb")
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    @PostMapping("/articles")
    @PreAuthorize("hasAnyRole('EMPLOYEE','IT_SUPPORT','ADMIN')")
    public ResponseEntity<ArticleResponse> createArticle(
            @Valid @RequestBody ArticleRequest articleRequest
    ) {
        ArticleResponse articleResponse = knowledgeBaseService.createArticle(articleRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleResponse);
    }

    /**
     * Update a knowledge article
     * PUT /api/kb/articles/{id}
     * Role: SUPPORT, ADMIN
     */
    @PutMapping("/articles/{id}")
    @PreAuthorize("hasAnyRole('IT_SUPPORT','ADMIN')")
    public  ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequest request
    ){
        ArticleResponse articleResponse = knowledgeBaseService.updateArticle(id, request);
        return ResponseEntity.ok(articleResponse);
    }

    /**
     * Delete (archive) a knowledge article
     * DELETE /api/kb/articles/{id}
     * Role: ADMIN
     */
    @DeleteMapping("/articles/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        knowledgeBaseService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Publish a knowledge article
     * POST /api/kb/articles/{id}/publish
     * Role: SUPPORT, ADMIN
     */
    @PostMapping("/articles/{id}/publish")
    @PreAuthorize("hasAnyRole('IT_SUPPORT','ADMIN')")
    public ResponseEntity<ArticleResponse> publishArticle(@PathVariable Long id) {
        ArticleResponse articleResponse = knowledgeBaseService.publishArticle(id);
        return ResponseEntity.ok(articleResponse);
    }

    /**
     * Get a single article
     * GET /api/kb/articles/{id}
     * Role: All authenticated users
     */
    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long id) {
        ArticleResponse articleResponse = knowledgeBaseService.getArticle(id);
        return ResponseEntity.ok(articleResponse);
    }

    /**
     * Search articles
     * POST /api/kb/search
     * Role: All authenticated users
     */
    @PostMapping("/search")
    public ResponseEntity<Page<ArticleResponse>> searchArticles(@RequestBody ArticleSearchDTO searchDTO) {
        Page<ArticleResponse> articleResponses = knowledgeBaseService.getAllArticles(searchDTO);
        return ResponseEntity.ok(articleResponses);
    }

    /**
     * Get most viewed articles
     * GET /api/kb/popular/viewed?limit=5
     * Role: All authenticated users
     */
    @GetMapping("/popular/viewed")
    public ResponseEntity<List<ArticleResponse>> getMostViewedArticles(@RequestParam(defaultValue = "5") int limit) {
        List<ArticleResponse> articleResponses = knowledgeBaseService.getMostViewedArticles(limit);
        return ResponseEntity.ok(articleResponses);
    }

    /**
     * Get most helpful articles
     * GET /api/kb/popular/helpful?limit=5
     * Role: All authenticated users
     */
    @GetMapping("/popular/helpful")
    public ResponseEntity<List<ArticleResponse>> getMostHelpfulArticles(@RequestParam(defaultValue = "5") int limit) {
        List<ArticleResponse> articleResponses = knowledgeBaseService.getMostHelpfulArticles(limit);
        return ResponseEntity.ok(articleResponses);
    }

    /**
     * Mark article as helpful
     * POST /api/kb/articles/{id}/helpful
     * Role: All authenticated users
     */
    @PostMapping("/articles/{id}/helpful")
    public ResponseEntity<Void> markArticleAsHelpful(@PathVariable Long id, @RequestBody boolean helpful) {
        knowledgeBaseService.markHelpful(id, helpful);
        return ResponseEntity.ok().build();
    }

    /**
     * Get articles by author
     * GET /api/kb/author/{authorId}
     * Role: All authenticated users
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<ArticleResponse>> getArticlesByAuthor(@PathVariable Long authorId) {
        List<ArticleResponse> articleResponses = knowledgeBaseService.getArticlesByAuthor(authorId);
        return ResponseEntity.ok(articleResponses);
    }
    /**
     * Get statistics
     * GET /api/kb/statistics
     * Role: SUPPORT, ADMIN
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN','IT_SUPPORT')")
    public ResponseEntity<KnowledgeBaseService.ArticleStatistics> getStatistics() {
        KnowledgeBaseService.ArticleStatistics statistics = knowledgeBaseService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
