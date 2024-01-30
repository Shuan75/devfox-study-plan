package com.devfox.devfoxstudy.controller;

import com.devfox.devfoxstudy.domain.constant.FormStatus;
import com.devfox.devfoxstudy.domain.constant.SearchType;
import com.devfox.devfoxstudy.dto.request.ArticleRequest;
import com.devfox.devfoxstudy.dto.response.ArticleResponse;
import com.devfox.devfoxstudy.dto.response.ArticleWithCommentsResponse;
import com.devfox.devfoxstudy.security.BoardPrincipal;
import com.devfox.devfoxstudy.service.ArticleService;
import com.devfox.devfoxstudy.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {
    private final ArticleService articleService;
    private final PaginationService paginationService;

    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType, // 検索語を受ける
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            // 何もしない時全体検索をしながら内容を時間順整列
            ModelMap map) {
        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        // paginationServiceを呼び出して具現した具現体を持ってくる

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values());
        // Valuesで全ての内容をArrayで渡す

        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map) {
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));

        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.articleCommentsResponse());
        map.addAttribute("totalCount", articleService.getArticleCount());
        map.addAttribute("searchTypeHashtag", SearchType.HASHTAG);
        return "articles/detail";
    }
    @GetMapping("/search-hashtag")
    public String searchArticleHashtag( // ViewNameだけ見せるためString
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        List<String> hashtags = articleService.getHashtags();

        map.addAttribute("articles", articles);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchType", SearchType.HASHTAG);
        // SearchType.HASHTAGだけ渡すため作成 => Viewは受けて処理だけをする

        return "articles/search-hashtag";
    }
    @GetMapping("/form")
    public String articleWriteForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);
        return "articles/form";
    }

    @PostMapping("/form")
    public String articleSaveWrite(ArticleRequest articleRequest,
                                   @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        articleService.saveArticle(articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId, ArticleRequest articleRequest,
                                @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        // boardPrincipalで作ったtoDtoがUserAccountDtoを返還
        // つまりboardPrincipalからDtoを作ってArticleRequestに込めて送る
        // => request, responseDtoはControllerだけ dtoはserviceLayerだけ
        articleService.updateArticle(articleId, articleRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles/" + articleId;
    }

    @PostMapping ("/{articleId}/delete")
    public String deleteArticle(@PathVariable Long articleId,
                                @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        // SecurityContextHolderを使わず、AuthenticationPrincipalに合わせて作ったBoardPrincipalを使う

        articleService.deleteArticle(articleId, boardPrincipal.getUsername());

        return "redirect:/articles";
    }

}
