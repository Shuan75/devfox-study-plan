package com.devfox.devfoxstudy.controller;

import com.devfox.devfoxstudy.domain.constant.SearchType;
import com.devfox.devfoxstudy.dto.UserAccountDto;
import com.devfox.devfoxstudy.dto.request.ArticleRequest;
import com.devfox.devfoxstudy.dto.response.ArticleResponse;
import com.devfox.devfoxstudy.dto.response.ArticleWithCommentsResponse;
import com.devfox.devfoxstudy.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType, // 検索語を受ける
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            // 何もしない時全体検索をしながら内容を時間順整列
            ModelMap map) {
        map.addAttribute("articles", articleService
                .searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from));
        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map) {
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));

        map.addAttribute("article", article);
        // #Todo Comment後で処理 
        map.addAttribute("totalCount", articleService.getArticleCount());
        map.addAttribute("searchTypeHashtag", SearchType.HASHTAG);
        return "articles/detail";
    }
    @GetMapping("/search-hashtag")
    public String searchArticleHashtag(
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<String> hashtags = articleService.getHashtags();

        map.addAttribute("articles", articles);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("searchType", SearchType.HASHTAG);

        return "articles/search-hashtag";
    }
    @GetMapping("/write")
    public String articleWriteForm(ModelMap map) {
        map.addAttribute("articles", List.of());
        return "articles/write";
    }

    @PostMapping("/write")
    public String articleSaveWrite(ArticleRequest articleRequest) {
        articleService.saveArticle(articleRequest
                .toDto(UserAccountDto.of("jong", "asdf1234", "elki751@gmail.com", "jong", "memo",
                        null, null, null, null)));

        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/write")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);

        return "articles/write";
    }

    @PostMapping ("/{articleId}/write")
    public String updateArticle(@PathVariable Long articleId, ArticleRequest articleRequest) {
        articleService.updateArticle(articleId, articleRequest
                .toDto(UserAccountDto.of("jong", "asdf1234", "elki751@gmail.com", "jong", "memo",
                        null, null, null, null)));

        return "redirect:/articles/" + articleId;
    }

    @PostMapping ("/{articleId}/delete")
    public String deleteArticle(@PathVariable Long articleId) {
        articleService.deleteArticle(articleId);

        return "redirect:/articles";
    }

}
