package com.help.desk.knowledgebase.controller;


import com.help.desk.exception.ApiResponse;
import com.help.desk.knowledgebase.dto.request.ArticleRequest;
import com.help.desk.knowledgebase.dto.response.ArticleResponse;
import com.help.desk.knowledgebase.service.KnowledgeBaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
