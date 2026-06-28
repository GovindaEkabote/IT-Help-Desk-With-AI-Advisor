package com.help.desk.knowledgebase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String category;

    private String tags;

    private String keywords;

    private String status; // DRAFT, PUBLISHED, ARCHIVED
}
