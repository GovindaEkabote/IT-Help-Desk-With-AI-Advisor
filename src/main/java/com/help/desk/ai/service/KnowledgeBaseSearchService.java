// src/main/java/com/help/desk/ai/service/KnowledgeBaseSearchService.java
package com.help.desk.ai.service;

import com.help.desk.knowledgebase.model.KnowledgeModel;
import com.help.desk.knowledgebase.model.KnowledgeModel.ArticleStatus;
import com.help.desk.knowledgebase.repository.KnowledgeBaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeBaseSearchService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    /**
     * Search knowledge base for relevant articles
     */
    public List<KnowledgeModel> searchKnowledgeBase(String query) {
        try {
            // ✅ FIXED: Use ArticleStatus.PUBLISHED instead of String
            List<KnowledgeModel> allArticles = knowledgeBaseRepository
                    .findByStatus(ArticleStatus.PUBLISHED)
                    .orElse(new ArrayList<>());

            log.info("Found {} published articles in knowledge base", allArticles.size());

            if (allArticles.isEmpty()) {
                log.info("No published articles found in knowledge base");
                return new ArrayList<>();
            }

            // Try exact keyword match first
            List<KnowledgeModel> exactMatches = knowledgeBaseRepository
                    .searchByStatusAndKeyword(ArticleStatus.PUBLISHED, query);

            if (!exactMatches.isEmpty()) {
                log.info("Found {} exact keyword matches for: {}", exactMatches.size(), query);
                return exactMatches.stream().limit(3).collect(Collectors.toList());
            }

            // Calculate relevance scores for all articles
            Map<KnowledgeModel, Double> scores = new HashMap<>();
            String[] queryWords = query.toLowerCase().split("\\s+");

            for (KnowledgeModel article : allArticles) {
                double score = calculateRelevanceScore(article, queryWords);
                if (score > 0) {
                    scores.put(article, score);
                }
            }

            // If no scores, try category matching
            if (scores.isEmpty()) {
                for (KnowledgeModel article : allArticles) {
                    if (article.getCategory() != null &&
                            query.toLowerCase().contains(article.getCategory().toLowerCase())) {
                        scores.put(article, 1.0);
                    }
                }
            }

            // Sort by score and return top 3
            return scores.entrySet().stream()
                    .sorted(Map.Entry.<KnowledgeModel, Double>comparingByValue().reversed())
                    .limit(3)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error searching knowledge base: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Calculate relevance score for an article
     */
    private double calculateRelevanceScore(KnowledgeModel article, String[] queryWords) {
        // Build searchable text
        String searchText = (article.getTitle() + " " +
                article.getContent() + " " +
                (article.getTags() != null ? article.getTags() : "") + " " +
                (article.getKeywords() != null ? article.getKeywords() : "")).toLowerCase();

        double score = 0.0;
        int wordMatchCount = 0;

        for (String word : queryWords) {
            if (word.length() < 2) continue; // Skip short words

            if (searchText.contains(word)) {
                long count = searchText.split(word, -1).length - 1;
                score += 1.0 + (count * 0.5);
                wordMatchCount++;
            }
        }

        // Bonus for matching multiple words
        if (wordMatchCount > 1) {
            score += (wordMatchCount * 0.5);
        }

        // Bonus for title matches (higher weight)
        String titleLower = article.getTitle().toLowerCase();
        for (String word : queryWords) {
            if (titleLower.contains(word)) {
                score += 3.0;
            }
        }

        // Bonus for category matches
        String categoryLower = article.getCategory() != null ?
                article.getCategory().toLowerCase() : "";
        for (String word : queryWords) {
            if (categoryLower.contains(word)) {
                score += 1.5;
            }
        }

        log.debug("Article '{}' scored: {}", article.getTitle(), score);
        return score;
    }

    /**
     * Get relevant articles as formatted text for AI context
     */
    public String getArticlesAsContext(String query) {
        List<KnowledgeModel> articles = searchKnowledgeBase(query);

        if (articles.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        context.append("Here are some relevant articles from our knowledge base:\n\n");

        for (int i = 0; i < articles.size(); i++) {
            KnowledgeModel article = articles.get(i);
            context.append("--- Article ").append(i + 1).append(": ").append(article.getTitle()).append(" ---\n");
            context.append("Category: ").append(article.getCategory()).append("\n");
            context.append("Content: ").append(article.getContent()).append("\n");

            if (article.getTags() != null && !article.getTags().isEmpty()) {
                context.append("Tags: ").append(article.getTags()).append("\n");
            }
            context.append("\n");
        }

        return context.toString();
    }

    /**
     * Get article titles for sources
     */
    public List<String> getArticleTitles(String query) {
        List<KnowledgeModel> articles = searchKnowledgeBase(query);
        return articles.stream()
                .map(KnowledgeModel::getTitle)
                .collect(Collectors.toList());
    }
}