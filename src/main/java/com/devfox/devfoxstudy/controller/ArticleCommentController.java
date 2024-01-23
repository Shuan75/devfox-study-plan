package com.devfox.devfoxstudy.controller;

import com.devfox.devfoxstudy.dto.UserAccountDto;
import com.devfox.devfoxstudy.dto.request.ArticleCommentRequest;
import com.devfox.devfoxstudy.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(
            ArticleCommentRequest articleCommentRequest
    ) {
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(UserAccountDto.of("jong", "asdf1234", "elki751@gmail.com", "jong", "memo",
                null, null, null, null)));

        return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(
            @PathVariable Long commentId,
            Long articleId
    ) {
        articleCommentService.deleteArticleComment(commentId, "jong");

        return "redirect:/articles/" + articleId;
    }

}
