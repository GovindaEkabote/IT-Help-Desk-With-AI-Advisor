package com.help.desk.knowledgebase.repository;

import com.help.desk.knowledgebase.model.KnowledgeModel;
import com.help.desk.knowledgebase.model.KnowledgeModel.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeModel, Long> {

    // Find published articles
    Page<KnowledgeModel> findByStatus(ArticleStatus status, Pageable pageable);

    // Find by category
    Page<KnowledgeModel> findByCategoryAndStatus(String category, ArticleStatus status, Pageable pageable);

    // search in title, content, keywords
    @Query("""
            SELECT k FROM KnowledgeModel k
                WHERE k.status = 'PUBLISHED'
                  AND (
                  LOWER(k.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                  OR LOWER(k.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                  OR LOWER(k.keywords) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                  OR LOWER(k.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                )
            """)
    Page<KnowledgeModel> searchPublished(
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    // Get most viewed
    @Query("SELECT k FROM KnowledgeModel k WHERE k.status = 'PUBLISHED' ORDER BY k.viewCount DESC")
    List<KnowledgeModel> findMostViewed(Pageable pageable);

    // Get most helpful
    @Query("""
            SELECT k FROM KnowledgeModel k WHERE 
            k.status = 'PUBLISHED' ORDER BY k.helpfulCount DESC
            """)
    List<KnowledgeModel> findMostHelpful(Pageable pageable);

    // Get by Author
    List<KnowledgeModel> findByAuthorId(Long authorId);



    // Check if title exists
    boolean existsByTitle(String title);

    // Get all published for RAG
    @Query("""
            SELECT k FROM KnowledgeModel k WHERE k.status = 'PUBLISHED'
            """)
    List<KnowledgeModel> findAllPublished();

    // Count by status
    long countByStatus(ArticleStatus status);

    // Search with category filter
    @Query("""
        SELECT k
        FROM KnowledgeModel k
        WHERE k.status = 'PUBLISHED'
        AND (:category IS NULL OR k.category = :category)   
        AND (
        LOWER(k.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(k.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(k.keywords) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        """)
    Page<KnowledgeModel> searchByCategoryAndTerm(
            @Param("category") String category,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

}
