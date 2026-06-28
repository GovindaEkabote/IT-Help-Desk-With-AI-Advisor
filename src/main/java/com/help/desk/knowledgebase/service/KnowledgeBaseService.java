package com.help.desk.knowledgebase.service;


import com.help.desk.auth.service.AuthService;
import com.help.desk.exception.ResourceNotFoundException;
import com.help.desk.knowledgebase.dto.ArticleSearchDTO;
import com.help.desk.knowledgebase.dto.request.ArticleRequest;
import com.help.desk.knowledgebase.dto.response.ArticleResponse;
import com.help.desk.knowledgebase.mapper.ArticleMapper;
import com.help.desk.knowledgebase.model.KnowledgeModel;
import com.help.desk.knowledgebase.model.KnowledgeModel.ArticleStatus;
import com.help.desk.knowledgebase.repository.KnowledgeBaseRepository;
import com.help.desk.user.model.User;
import com.help.desk.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBaseService {

    private final KnowledgeBaseRepository repository;
    private final ArticleMapper articleMapper;
    private final UserService userService;
    private final AuthService authService;

    /*
     Create a new knowledge article
    */
    @Transactional
    public ArticleResponse createArticle(ArticleRequest request) {
        User user = authService.getCurrentUser();

        if (repository.existsByTitle(request.getTitle())){
            throw new RuntimeException("Article with title '" + request.getTitle() + "' already exists");
        }

        KnowledgeModel article = articleMapper.toEntity(request, user);
        KnowledgeModel savedArticle = repository.save(article);

        log.info("Knowledge article created: {} by {}", savedArticle.getTitle(), user.getFirstName());
        return articleMapper.toResponseDTO(savedArticle);
    }

    /*
    * Update an existing article
    */

    @Transactional
    public ArticleResponse updateArticle(Long id, ArticleRequest request){
        KnowledgeModel article = repository.findById(id)
                .orElseThrow(() ->new ResourceNotFoundException("Article not found with id"));

        User  currentUser = authService.getCurrentUser();
        articleMapper.updateEntity(article, request, currentUser);

        KnowledgeModel updatedArticle = repository.save(article);
        log.info("Knowledge article updated: {} by {}", updatedArticle.getTitle(), currentUser.getEmail());

        return articleMapper.toResponseDTO(updatedArticle);

    }

    /**
     * Delete (archive) an article
     */
    @Transactional
    public void deleteArticle(Long id){
        KnowledgeModel article = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));

        article.setStatus(ArticleStatus.ARCHIVED);
        article.setUpdatedBy(authService.getCurrentUser().getId());
        repository.save(article);

        log.info("Knowledge article archived: {}", id);
    }

    /**
     * Publish an article
     */
    @Transactional
    public ArticleResponse publishArticle(Long id) {
        KnowledgeModel article = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));

        article.setStatus(ArticleStatus.PUBLISHED);
        article.setLastReviewedAt(LocalDateTime.now());
        article.setUpdatedBy(authService.getCurrentUser().getId());

        KnowledgeModel publishedArticle = repository.save(article);
        log.info("Knowledge article published: {}", publishedArticle.getTitle());

        return articleMapper.toResponseDTO(publishedArticle);
    }


    /**
     * Get article by ID (increments view count)
     */
    public ArticleResponse getArticle(Long id) {
        KnowledgeModel article = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));

        // Only increment views for published articles
        if (article.getStatus() == ArticleStatus.PUBLISHED) {
            article.setViewCount(article.getViewCount() + 1);
            repository.save(article);
        }

        return articleMapper.toResponseDTO(article);
    }

    /*
     * Get all articles with search/filter
     */

    public Page<ArticleResponse> getAllArticles(ArticleSearchDTO searchDTO){
        Pageable pageable = PageRequest.of(
                searchDTO.getPage(),
                searchDTO.getSize(),
                Sort.by(Sort.Direction.fromString(searchDTO.getSortDirection()), searchDTO.getSortBy())
        );

        Page<KnowledgeModel> articles;

        if(searchDTO.getSearchTerm() != null && !searchDTO.getSearchTerm().isEmpty()){
            if(searchDTO.getCategory() != null && !searchDTO.getCategory().isEmpty()){
                articles = repository.searchByCategoryAndTerm(
                        searchDTO.getCategory(),
                        searchDTO.getSearchTerm(),
                        pageable
                );
            }else{
                articles = repository.searchPublished(searchDTO.getSearchTerm(), pageable);
            }
        } else if (searchDTO.getCategory() != null && !searchDTO.getCategory().isEmpty()) {
            articles = repository.findByCategoryAndStatus(
                    searchDTO.getCategory(),
                    ArticleStatus.PUBLISHED,
                    pageable
            );
        }else {
            // Get all published
            articles = repository.findByStatus(ArticleStatus.PUBLISHED, pageable);
        }
        return articles.map(articleMapper::toResponseDTO);
    }

    /**
     * Get most viewed articles
     */
    public List<ArticleResponse> getMostViewedArticles(int limit){
        Pageable pageable = PageRequest.of(0, limit);
        List<KnowledgeModel> articles = repository.findMostViewed(pageable);
        return articles.stream()
                .map(articleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get most helpful articles
     */
    public List<ArticleResponse> getMostHelpfulArticles(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<KnowledgeModel> articles = repository.findMostHelpful(pageable);
        return articles.stream()
                .map(articleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mark article as helpful or not helpful
     */
    @Transactional
    public void markHelpful(Long id, boolean helpful) {
        KnowledgeModel article = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));

        if (helpful) {
            article.setHelpfulCount(article.getHelpfulCount() + 1);
        } else {
            article.setNotHelpfulCount(article.getNotHelpfulCount() + 1);
        }

        repository.save(article);
        log.info("Article {} marked as {}helpful", id, helpful ? "" : "not ");
    }

    /**
     * Get all published articles (for RAG/AI)
     */
    public List<KnowledgeModel> getAllPublishedArticles() {
        return repository.findAllPublished();
    }

    /**
     * Get articles by author
     */
    public List<ArticleResponse> getArticlesByAuthor(Long authorId) {
        List<KnowledgeModel> articles = repository.findByAuthorId(authorId);
        return articles.stream()
                .map(articleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get statistics
     */
    public ArticleStatistics getStatistics() {
        ArticleStatistics stats = new ArticleStatistics();
        stats.setTotalArticles(repository.count());
        stats.setPublishedArticles(repository.countByStatus(ArticleStatus.PUBLISHED));
        stats.setDraftArticles(repository.countByStatus(ArticleStatus.DRAFT));
        stats.setArchivedArticles(repository.countByStatus(ArticleStatus.ARCHIVED));
        return stats;
    }

    @lombok.Data
    public static class ArticleStatistics{
        private long totalArticles;
        private long publishedArticles;
        private long draftArticles;
        private long archivedArticles;
    }
}
