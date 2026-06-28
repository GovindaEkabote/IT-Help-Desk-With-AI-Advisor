package com.help.desk.knowledgebase.mapper;

import com.help.desk.knowledgebase.dto.request.ArticleRequest;
import com.help.desk.knowledgebase.dto.response.ArticleResponse;
import com.help.desk.knowledgebase.model.KnowledgeModel;
import com.help.desk.knowledgebase.model.KnowledgeModel.ArticleStatus;
import com.help.desk.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {

    public KnowledgeModel toEntity(ArticleRequest dto, User author) {
        KnowledgeModel article = new KnowledgeModel();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory());
        article.setTags(dto.getTags());
        article.setKeywords(dto.getKeywords());
        article.setAuthor(author);
        article.setCreatedBy(author.getId());
        article.setUpdatedBy(author.getId());

        if (dto.getStatus() != null) {
            try {
                article.setStatus(ArticleStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                article.setStatus(ArticleStatus.DRAFT);
            }
        } else {
            article.setStatus(ArticleStatus.DRAFT);
        }

        return article;
    }

    public ArticleResponse toResponseDTO(KnowledgeModel article) {
        ArticleResponse dto = new ArticleResponse();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setCategory(article.getCategory());
        dto.setTags(article.getTags());
        dto.setKeywords(article.getKeywords());
        dto.setViewCount(article.getViewCount());
        dto.setHelpfulCount(article.getHelpfulCount());
        dto.setNotHelpfulCount(article.getNotHelpfulCount());
        dto.setStatus(article.getStatus().name());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        dto.setCreatedBy(article.getCreatedBy());
        dto.setUpdatedBy(article.getUpdatedBy());

        if (article.getAuthor() != null) {
            dto.setAuthorId(article.getAuthor().getId());
            dto.setAuthorName(article.getAuthor().getFirstName() + " " +
                    article.getAuthor().getLastName());
        }

        return dto;
    }

    public void updateEntity(KnowledgeModel article, ArticleRequest dto, User updater) {
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory());
        article.setTags(dto.getTags());
        article.setKeywords(dto.getKeywords());
        article.setUpdatedBy(updater.getId());

        if (dto.getStatus() != null) {
            try {
                article.setStatus(ArticleStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                // Keep existing status
            }
        }
    }

}
