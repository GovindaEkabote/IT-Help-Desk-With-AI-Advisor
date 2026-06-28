package com.help.desk.knowledgebase.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleResponse {

    private Long id;
    private String title;
    private String content;
    private String category;
    private String tags;
    private String keywords;
    private String authorName;
    private Long authorId;
    private Integer viewCount;
    private Integer helpfulCount;
    private Integer notHelpfulCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
