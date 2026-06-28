package com.help.desk.knowledgebase.dto;

import lombok.Data;

@Data
public class ArticleSearchDTO {

    private String searchTerm;
    private String category;
    private String status = "PUBLISHED";
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}
